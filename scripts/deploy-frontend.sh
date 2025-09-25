#!/bin/bash

# ReserveEase Frontend Deployment Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
FRONTEND_DIR="../frontend"
BUILD_DIR="$FRONTEND_DIR/build"

echo -e "${GREEN}🌐 ReserveEase Frontend Deployment${NC}"
echo "===================================="

# Check if Node.js and npm are installed
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js not found. Please install Node.js.${NC}"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo -e "${RED}❌ npm not found. Please install npm.${NC}"
    exit 1
fi

# Navigate to frontend directory
cd "$FRONTEND_DIR"

echo -e "${YELLOW}📦 Installing dependencies...${NC}"
npm install

echo -e "${YELLOW}🔨 Building frontend application...${NC}"
npm run build

# Check if build was successful
if [ ! -d "$BUILD_DIR" ]; then
    echo -e "${RED}❌ Build directory not found. Build may have failed.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Frontend built successfully${NC}"

# Check if infrastructure outputs exist
OUTPUTS_FILE="../infrastructure/deployment-outputs.json"
if [ ! -f "$OUTPUTS_FILE" ]; then
    echo -e "${RED}❌ Infrastructure outputs not found. Deploy infrastructure first.${NC}"
    exit 1
fi

# Extract deployment information
S3_BUCKET=$(jq -r '.s3_bucket_name.value' "$OUTPUTS_FILE")
CLOUDFRONT_ID=$(jq -r '.cloudfront_distribution_id.value' "$OUTPUTS_FILE")
DOMAIN_NAME=$(jq -r '.application_url.value' "$OUTPUTS_FILE")

echo -e "${YELLOW}☁️  Uploading to S3...${NC}"

# Upload build files to S3
aws s3 sync "$BUILD_DIR" "s3://$S3_BUCKET" --delete

echo -e "${GREEN}✅ Files uploaded to S3${NC}"

echo -e "${YELLOW}🔄 Invalidating CloudFront cache...${NC}"

# Invalidate CloudFront cache
aws cloudfront create-invalidation --distribution-id "$CLOUDFRONT_ID" --paths "/*"

echo -e "${GREEN}✅ CloudFront cache invalidated${NC}"

echo -e "${GREEN}🎉 Frontend deployment completed!${NC}"
echo ""
echo -e "${GREEN}🌍 Your application will be available at: $DOMAIN_NAME${NC}"
echo -e "${YELLOW}Note: CloudFront invalidation may take a few minutes to complete.${NC}"
