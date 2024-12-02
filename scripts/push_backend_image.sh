    aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin "$AWS_ACCOUNT_ID".dkr.ecr.eu-central-1.amazonaws.com
    docker build --platform=linux/amd64 -t "$AWS_ACCOUNT_ID".dkr.ecr.eu-central-1.amazonaws.com/taskappapi:latest .
    docker push "$AWS_ACCOUNT_ID".dkr.ecr.eu-central-1.amazonaws.com/taskappapi:latest
    docker logout "$AWS_ACCOUNT_ID".dkr.ecr.eu-central-1.amazonaws.com