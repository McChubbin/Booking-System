#!/bin/bash

# ReserveEase Backend Deployment Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
BACKEND_DIR="../backend"
BUILD_DIR="$BACKEND_DIR/target"
JAR_FILE="reservation-system-0.0.1-SNAPSHOT.jar"
RENAMED_JAR="reserveease-backend.jar"

echo -e "${GREEN}🖥️  ReserveEase Backend Deployment${NC}"
echo "====================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven not found. Please install Maven.${NC}"
    exit 1
fi

# Navigate to backend directory
cd "$BACKEND_DIR"

echo -e "${YELLOW}🔨 Building backend application...${NC}"

# Clean and build the application
mvn clean package -DskipTests

# Check if JAR was built successfully
if [ ! -f "$BUILD_DIR/$JAR_FILE" ]; then
    echo -e "${RED}❌ JAR file not found. Build may have failed.${NC}"
    exit 1
fi

# Rename JAR file for deployment
cp "$BUILD_DIR/$JAR_FILE" "$BUILD_DIR/$RENAMED_JAR"

echo -e "${GREEN}✅ Backend built successfully${NC}"

# Check if infrastructure outputs exist
OUTPUTS_FILE="../infrastructure/deployment-outputs.json"
if [ ! -f "$OUTPUTS_FILE" ]; then
    echo -e "${RED}❌ Infrastructure outputs not found. Deploy infrastructure first.${NC}"
    exit 1
fi

# Extract deployment information
S3_BUCKET=$(jq -r '.s3_bucket_name.value' "$OUTPUTS_FILE")
ALB_DNS=$(jq -r '.load_balancer_dns.value' "$OUTPUTS_FILE")

echo -e "${YELLOW}📦 Deploying backend to AWS...${NC}"

# Upload JAR to S3 for deployment
aws s3 cp "$BUILD_DIR/$RENAMED_JAR" "s3://$S3_BUCKET-deployment/$RENAMED_JAR"

echo -e "${GREEN}✅ Backend JAR uploaded to S3${NC}"

# You would typically use AWS CodeDeploy or similar service here
# For now, we'll provide instructions for manual deployment

echo -e "${YELLOW}📝 Manual deployment steps:${NC}"
echo "1. SSH into your EC2 instances"
echo "2. Download the JAR from S3:"
echo "   aws s3 cp s3://$S3_BUCKET-deployment/$RENAMED_JAR /opt/reserveease/$RENAMED_JAR"
echo "3. Restart the service:"
echo "   sudo systemctl restart reserveease"
echo ""
echo "Backend endpoint will be available at: https://$ALB_DNS/api"

echo -e "${GREEN}🎉 Backend deployment completed!${NC}"
