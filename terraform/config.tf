# config.tf
provider "aws" {
  region  = "eu-central-1"
}

terraform {
  required_version = ">= 1.0"

  backend "s3" {
    bucket = "terraform-state-kacper-jablonski-task-list"
    key = "global/s3/terraform.tfstate"
    region = "eu-central-1"
    encrypt = true
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.69.0"
    }
  }
}