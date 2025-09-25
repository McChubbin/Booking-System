#!/bin/bash

# ReserveEase Infrastructure Deployment Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
TERRAFORM_DIR="../infrastructure/terraform"
AWS_REGION="us-east-1"
DOMAIN_NAME="reserveease.com"
APP_NAME="reserveease"

echo -e "${GREEN}🏗️  ReserveEase Infrastructure Deployment${NC}"
echo "=========================================="

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"

if ! command -v terraform &> /dev/null; then
    echo -e "${RED}❌ Terraform not found. Please install Terraform.${NC}"
    exit 1
fi

if ! command -v aws &> /dev/null; then
    echo -e "${RED}❌ AWS CLI not found. Please install AWS CLI.${NC}"
    exit 1
fi

# Check AWS credentials
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}❌ AWS credentials not configured. Please run 'aws configure'.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Prerequisites check passed${NC}"

# Generate SSH key pair if it doesn't exist
if [ ! -f ~/.ssh/id_rsa ]; then
    echo -e "${YELLOW}🔑 Generating SSH key pair...${NC}"
    ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N ""
    echo -e "${GREEN}✅ SSH key pair generated${NC}"
fi

# Navigate to Terraform directory
cd "$TERRAFORM_DIR"

# Prompt for database password
echo -e "${YELLOW}🔐 Please enter a secure database password:${NC}"
read -s db_password

if [ -z "$db_password" ]; then
    echo -e "${RED}❌ Database password cannot be empty${NC}"
    exit 1
fi

# Initialize Terraform
echo -e "${YELLOW}🔧 Initializing Terraform...${NC}"
terraform init

# Plan the deployment
echo -e "${YELLOW}📋 Planning infrastructure...${NC}"
terraform plan \
    -var="aws_region=$AWS_REGION" \
    -var="domain_name=$DOMAIN_NAME" \
    -var="app_name=$APP_NAME" \
    -var="db_password=$db_password"

# Confirm deployment
echo -e "${YELLOW}❓ Do you want to proceed with the deployment? (y/N)${NC}"
read -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}⏹️  Deployment cancelled${NC}"
    exit 0
fi

# Apply the infrastructure
echo -e "${GREEN}🚀 Deploying infrastructure...${NC}"
terraform apply -auto-approve \
    -var="aws_region=$AWS_REGION" \
    -var="domain_name=$DOMAIN_NAME" \
    -var="app_name=$APP_NAME" \
    -var="db_password=$db_password"

echo -e "${GREEN}✅ Infrastructure deployment completed!${NC}"
echo ""
echo -e "${YELLOW}📝 Next steps:${NC}"
terraform output -raw setup_instructions

# Save outputs to file
echo -e "${YELLOW}💾 Saving deployment outputs...${NC}"
terraform output -json > ../deployment-outputs.json

echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
echo -e "${GREEN}Check deployment-outputs.json for important resource information.${NC}"
