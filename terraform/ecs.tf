resource "aws_ecs_cluster" "TaskListApp" {
   name = "TaskListApp"
}

# BACKEND SERVICE
resource "aws_ecs_service" "TaskListApi" {
  name            = "TaskListApi"
  task_definition = aws_ecs_task_definition.TaskListApi.arn
  launch_type = "FARGATE"
  cluster = "TaskListApp"

  network_configuration {
  assign_public_ip = false

    security_groups = [
     aws_security_group.lb_sg.id,
     aws_security_group.ingress_api_sg.id,
   ]

   subnets = [
     aws_subnet.private_a.id,
     aws_subnet.private_b.id,
   ]
 }

   load_balancer {
     target_group_arn = aws_lb_target_group.TaskListApi.arn
     container_name   = "TaskListApi"
     container_port   = "8080"
}
  desired_count = 1
}

# FRONTEND SERVICE
resource "aws_ecs_service" "TaskListFrontend" {
  name            = "TaskListFrontend"
  task_definition = aws_ecs_task_definition.TaskListFrontend.arn
  launch_type = "FARGATE"
  cluster = "TaskListApp"

  network_configuration {
    assign_public_ip = false

    security_groups = [
      aws_security_group.lb_sg.id,
      aws_security_group.ingress_frontend_sg.id,
    ]

    subnets = [
      aws_subnet.private_a.id,
      aws_subnet.private_b.id,
    ]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.TaskListFrontend.arn
    container_name   = "TaskListFrontend"
    container_port   = "3000"
  }
  desired_count = 1
}


resource "aws_cloudwatch_log_group" "TaskListApi" {
  name = "/ecs/TaskListApi"
}

locals {
  db_url = format("%s://%s/%s","jdbc:postgresql", aws_db_instance.TaskListApi.endpoint, "postgres?currentSchema=test")
}

# BACKEND TASK DEFINITION
resource "aws_ecs_task_definition" "TaskListApi" {
  family = "TaskListApi"

  execution_role_arn = aws_iam_role.TaskListApi_task_execution_role.arn

  container_definitions = <<EOF
  [
    {
      "name": "TaskListApi",
      "image": "357752156930.dkr.ecr.eu-central-1.amazonaws.com/taskappapi:latest",
      "environment": [
        { "name" : "SPRING_DATASOURCE_URL", "value" : "${local.db_url}"},
        { "name" : "SPRING_DATASOURCE_USERNAME", "value" : "${aws_ssm_parameter.db_username.value}"},
        { "name" : "SPRING_DATASOURCE_PASSWORD", "value" : "${aws_ssm_parameter.db_password.value}"}
      ],
      "portMappings": [
        {
          "hostPort": 8080,
          "protocol": "tcp",
          "containerPort": 8080
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-region": "eu-central-1",
          "awslogs-group": "/ecs/TaskListApi",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
  EOF

  cpu = 256
  memory = 512
  requires_compatibilities = ["FARGATE"]

  network_mode = "awsvpc"
}

# FRONTEND TASK DEFINITION
resource "aws_ecs_task_definition" "TaskListFrontend" {
  family = "TaskListFrontend"

  execution_role_arn = aws_iam_role.TaskListApi_task_execution_role.arn

  container_definitions = <<EOF
  [
    {
      "name": "TaskListFrontend",
      "image": "357752156930.dkr.ecr.eu-central-1.amazonaws.com/testapp:latest",
      "portMappings": [
        {
          "hostPort": 3000,
          "protocol": "tcp",
          "containerPort": 3000
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-region": "eu-central-1",
          "awslogs-group": "/ecs/TaskListApi",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
  EOF

  cpu = 512
  memory = 1024
  requires_compatibilities = ["FARGATE"]

  network_mode = "awsvpc"
}


resource "aws_iam_role" "TaskListApi_task_execution_role" {
  name               = "TaskListApi-task-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_assume_role.json
}

data "aws_iam_policy_document" "ecs_task_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

data "aws_iam_policy" "ecs_task_execution_role" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role" {
  role       = aws_iam_role.TaskListApi_task_execution_role.name
  policy_arn = data.aws_iam_policy.ecs_task_execution_role.arn
}

resource "aws_lb_target_group" "TaskListApi" {
  name        = "sun-TaskListApi"
  port        = 8080
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.app_vpc.id

  health_check {
    enabled = true
    path    = "/actuator/health"
    interval = 60
    timeout = 20
  }

  depends_on = [aws_alb.TaskListApi]
}

resource "aws_lb_target_group" "TaskListFrontend" {
  name        = "sun-TaskListFrontend"
  port        = 3000
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.app_vpc.id

  health_check {
    enabled = true
    path    = "/"
    interval = 60
    timeout = 20
    matcher = "200,304"
  }

  depends_on = [aws_alb.TaskListApi]
}

resource "aws_alb" "TaskListApi" {
  name               = "TaskListApi-lb"
  internal           = false
  load_balancer_type = "application"

  subnets = [
    aws_subnet.public_a.id,
    aws_subnet.public_b.id,
  ]

  security_groups = [
    aws_security_group.lb_sg.id,
  ]

  depends_on = [aws_internet_gateway.igw]
}

data "aws_acm_certificate" "certificate" {
  domain   = "task-list-kacper-jablonski.sparkbit.pl"
  statuses = ["ISSUED"]
}

resource "aws_alb_listener" "TaskListApi_https" {
  load_balancer_arn = aws_alb.TaskListApi.arn
  port              = "443"
  protocol          = "HTTPS"
  certificate_arn = data.aws_acm_certificate.certificate.arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.TaskListApi.arn
  }
}


resource "aws_alb_listener_rule" "ForwardBackend" {
  listener_arn = aws_alb_listener.TaskListApi_https.arn
  priority     = 1

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.TaskListApi.arn
  }

  condition {
    path_pattern {
      values = ["/actuator*", "/api/*", "/swagger-ui*", "/v3/api-docs*"]
    }
  }
}

resource "aws_alb_listener_rule" "ForwardFrontend" {
  listener_arn = aws_alb_listener.TaskListApi_https.arn
  priority     = 2

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.TaskListFrontend.arn
  }

  condition {
    path_pattern {
      values = ["/*"]
    }
  }
}

resource "aws_security_group" "rds" {
  name   = "TaskListApi_rds"
  vpc_id = aws_vpc.app_vpc.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    security_groups = [aws_security_group.lb_sg.id]
  }

  egress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "TaskListApi_rds"
  }
}

resource "aws_db_subnet_group" "rdssubnet" {
  name       = "rdssubnet"
  subnet_ids = [aws_subnet.public_a.id, aws_subnet.public_b.id]

  tags = {
    Name = "rdssubnet"
  }
}

resource "aws_ssm_parameter" "db_username" {
  name        = "db_username"
  type        = "SecureString"
  value       = var.db_username
}

resource "aws_ssm_parameter" "db_password" {
  name        = "db_password"
  type        = "SecureString"
  value       = var.db_password
}

resource "aws_db_instance" "TaskListApi" {
  identifier             = "tasklistapi"
  instance_class         = "db.t3.micro"
  allocated_storage      = 5
  engine                 = "postgres"
  engine_version         = "14.1"
  username               = aws_ssm_parameter.db_username.value
  password               = aws_ssm_parameter.db_password.value
  db_subnet_group_name   = aws_db_subnet_group.rdssubnet.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false
  skip_final_snapshot    = true
}


output "alb_url" {
  value = "http://${aws_alb.TaskListApi.dns_name}"
}