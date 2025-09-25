# ReserveEase Deployment Scripts

This directory contains automated deployment scripts for ReserveEase infrastructure and applications.

## Scripts

### 🏗️ `deploy-infrastructure.sh`
Deploys the complete AWS infrastructure using Terraform:
- VPC and networking
- RDS MySQL database
- EC2 instances with Auto Scaling
- Application Load Balancer
- S3 and CloudFront for frontend
- SSL certificates

**Prerequisites:**
- AWS CLI configured
- Terraform installed
- SSH key pair generated

**Usage:**
```bash
chmod +x deploy-infrastructure.sh
./deploy-infrastructure.sh
```

### 🖥️ `deploy-backend.sh`
Builds and deploys the Spring Boot backend:
- Compiles Java application
- Creates deployable JAR
- Uploads to S3
- Provides deployment instructions

**Prerequisites:**
- Maven installed
- Infrastructure deployed
- AWS CLI configured

**Usage:**
```bash
chmod +x deploy-backend.sh
./deploy-backend.sh
```

### 🌐 `deploy-frontend.sh`
Builds and deploys the React frontend:
- Installs npm dependencies
- Builds React application
- Uploads to S3
- Invalidates CloudFront cache

**Prerequisites:**
- Node.js and npm installed
- Infrastructure deployed
- AWS CLI configured

**Usage:**
```bash
chmod +x deploy-frontend.sh
./deploy-frontend.sh
```

## Environment Setup

1. **AWS Configuration**
   ```bash
   aws configure
   ```

2. **Generate SSH Key (if needed)**
   ```bash
   ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa
   ```

3. **Install Dependencies**
   ```bash
   # Terraform
   curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo apt-key add -
   sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main"
   sudo apt-get update && sudo apt-get install terraform

   # Node.js (using nvm)
   curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
   nvm install node

   # Maven
   sudo apt install maven
   ```

## Deployment Order

1. **Infrastructure First**: `./deploy-infrastructure.sh`
2. **Backend Second**: `./deploy-backend.sh`
3. **Frontend Last**: `./deploy-frontend.sh`

## Post-Deployment

After successful deployment:

1. **DNS Setup**: Point your domain to CloudFront distribution
2. **SSL Validation**: Complete ACM certificate validation
3. **SES Setup**: Verify sender emails in AWS SES
4. **Database Migration**: Run any required database scripts

## Troubleshooting

### Common Issues

**Terraform fails with permissions error:**
- Ensure AWS credentials have sufficient permissions
- Check IAM policies for EC2, RDS, S3, CloudFront access

**Frontend deployment timeout:**
- Check CloudFront invalidation status
- Verify S3 bucket permissions

**Backend not starting:**
- Check EC2 instance logs: `sudo journalctl -u reserveease -f`
- Verify database connection
- Ensure JAR file is properly deployed

### Useful Commands

```bash
# Check Terraform state
cd ../infrastructure/terraform
terraform show

# View infrastructure outputs
terraform output

# Check EC2 instance status
aws ec2 describe-instances --filters "Name=tag:Name,Values=reserveease-*"

# View CloudFront distributions
aws cloudfront list-distributions

# Check S3 bucket contents
aws s3 ls s3://your-frontend-bucket-name
```

## Monitoring

Access application logs and metrics:

- **CloudWatch Logs**: View application logs
- **CloudWatch Metrics**: Monitor performance
- **Load Balancer**: Check health of instances
- **RDS**: Monitor database performance

---

For support: admin@reserveease.com
