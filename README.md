# ReserveEase

🏖️ **ReserveEase** - Your gateway to effortless, free reservations!

A complete reservation management system built with React and Spring Boot, designed to make booking accommodations, events, and more completely free and transparent.

## ✨ Features

- **🆓 Completely Free** - No booking fees, no hidden charges
- **⚡ Lightning Fast** - Quick and easy booking process with instant confirmation
- **🔍 Transparent & Open** - See all bookings from other users for better planning
- **📱 Responsive Design** - Works perfectly on desktop, tablet, and mobile
- **🔐 Secure Authentication** - JWT-based user authentication
- **📧 Email Notifications** - Automated confirmation and update emails
- **☁️ AWS-Ready** - Infrastructure as code with Terraform

## 🏗️ Architecture

### Frontend (React)
- **Framework**: React 18 with React Bootstrap
- **Routing**: React Router v6
- **Authentication**: Context-based auth with JWT
- **Styling**: Bootstrap 5 with custom CSS
- **Deployment**: S3 + CloudFront

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.x with Spring Security
- **Database**: MySQL 8.0
- **Authentication**: JWT tokens
- **Email**: Spring Mail with AWS SES
- **Deployment**: EC2 with Auto Scaling

### Infrastructure (AWS)
- **Compute**: EC2 instances with Auto Scaling Groups
- **Database**: RDS MySQL with Multi-AZ
- **Storage**: S3 for static assets
- **CDN**: CloudFront distribution
- **Load Balancing**: Application Load Balancer
- **Monitoring**: CloudWatch logs and metrics

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ and npm
- Java 17+
- Maven 3.6+
- AWS CLI configured
- Terraform 1.0+

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ReservationSystem
   ```

2. **Start the backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Start the frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api

### AWS Deployment

See the comprehensive [AWS Hosting Guide](#-aws-hosting) below for detailed deployment instructions.

## 📁 Project Structure

```
ReservationSystem/
├── frontend/                 # React application
│   ├── public/
│   ├── src/
│   │   ├── components/       # React components
│   │   ├── contexts/         # React contexts
│   │   ├── services/         # API services
│   │   └── App.js
│   └── package.json
├── backend/                  # Spring Boot application
│   ├── src/main/java/
│   │   └── com/cottage/reservation/
│   │       ├── controller/   # REST controllers
│   │       ├── entity/       # JPA entities
│   │       ├── repository/   # Data repositories
│   │       ├── service/      # Business logic
│   │       └── config/       # Configuration
│   └── pom.xml
├── infrastructure/           # AWS infrastructure
│   └── terraform/           # Terraform configurations
├── scripts/                 # Deployment scripts
└── README.md
```

## ☁️ AWS Hosting

ReserveEase is designed for production deployment on AWS with enterprise-grade infrastructure. The complete setup includes auto-scaling, load balancing, SSL certificates, and comprehensive monitoring.

### 🏗️ Infrastructure Overview

| Service | Purpose | Configuration |
|---------|---------|---------------|
| **VPC** | Network isolation | 10.0.0.0/16 with public/private subnets |
| **EC2** | Backend hosting | Auto Scaling Group (1-3 instances) |
| **RDS** | MySQL database | Multi-AZ deployment with backups |
| **S3** | Frontend hosting | Static website with versioning |
| **CloudFront** | CDN | Global content delivery |
| **ALB** | Load balancing | SSL termination and health checks |
| **Route 53** | DNS management | Domain routing and health checks |
| **ACM** | SSL certificates | Free SSL/TLS certificates |
| **SES** | Email service | Reservation confirmations |
| **CloudWatch** | Monitoring | Logs, metrics, and alarms |

### 💰 Cost Estimates

| Environment | Monthly Cost | Components |
|-------------|-------------|------------|
| **Development** | $25-35 | t3.micro instances, minimal traffic |
| **Production** | $75-150 | t3.small instances, higher availability |
| **Enterprise** | $200-400 | t3.medium+ instances, enhanced monitoring |

*Costs vary based on traffic, data transfer, and storage usage.*

### 🚀 Quick Deploy

```bash
# 1. Deploy infrastructure
cd scripts && chmod +x *.sh
./deploy-infrastructure.sh

# 2. Deploy backend
./deploy-backend.sh

# 3. Deploy frontend  
./deploy-frontend.sh
```

### 📋 Detailed Deployment Guide

#### **Prerequisites**
- AWS CLI configured with appropriate permissions
- Domain name registered (optional but recommended)
- SSH key pair for EC2 access
- Terraform v1.0+ installed

#### **Step 1: Infrastructure Setup**

```bash
# Configure AWS credentials
aws configure

# Generate SSH key pair (if needed)
ssh-keygen -t rsa -b 4096 -f ~/.ssh/reserveease-key

# Deploy infrastructure
cd scripts
./deploy-infrastructure.sh
```

The script will:
- Create VPC with public/private subnets
- Deploy RDS MySQL database
- Set up Auto Scaling Groups and Load Balancer
- Configure S3 and CloudFront
- Provision SSL certificates

#### **Step 2: Domain Configuration**

If using a custom domain:

```bash
# Update Terraform variables
export DOMAIN_NAME="yourdomain.com"

# Get CloudFront distribution domain
aws cloudfront list-distributions --query 'DistributionList.Items[0].DomainName'

# Create DNS records in your domain registrar:
# CNAME: yourdomain.com -> d1234567890.cloudfront.net  
# CNAME: www.yourdomain.com -> d1234567890.cloudfront.net
```

#### **Step 3: SSL Certificate Validation**

```bash
# Check certificate status
aws acm list-certificates --region us-east-1

# Add DNS validation records shown in ACM console
# Wait for validation (5-10 minutes)
```

#### **Step 4: Backend Deployment**

```bash
# Build and deploy Spring Boot application
./deploy-backend.sh

# Check deployment status
aws autoscaling describe-auto-scaling-groups --auto-scaling-group-names reserveease-backend-asg

# View application logs
aws logs describe-log-groups --log-group-name-prefix "/aws/ec2/reserveease"
```

#### **Step 5: Frontend Deployment**

```bash
# Build and deploy React application
./deploy-frontend.sh

# Verify deployment
curl -I https://yourdomain.com
# Should return 200 OK with security headers
```

#### **Step 6: Email Configuration**

```bash
# Verify email addresses in SES
aws sesv2 put-email-identity --email-identity admin@yourdomain.com

# Check verification status
aws sesv2 get-email-identity --email-identity admin@yourdomain.com
```

### 🔧 Infrastructure Configuration

#### **Environment Variables**

The deployment automatically configures:

```yaml
# Backend (EC2)
DB_HOST: "rds-endpoint.region.rds.amazonaws.com"
DB_NAME: "reserveease"
DB_USERNAME: "reserveease_admin"
DB_PASSWORD: "secure-generated-password"
JWT_SECRET: "ReserveEaseSecretKey2024"
SERVER_PORT: "8080"
SPRING_PROFILES_ACTIVE: "production"

# Frontend (S3/CloudFront)  
REACT_APP_API_URL: "https://yourdomain.com/api"
REACT_APP_APP_NAME: "ReserveEase"
```

#### **Auto Scaling Configuration**

```hcl
# Backend Auto Scaling
Min Capacity: 1 instance
Max Capacity: 3 instances  
Desired: 2 instances
Scale Up: CPU > 70% for 2 minutes
Scale Down: CPU < 30% for 5 minutes
Health Check: /actuator/health
```

#### **Database Configuration**

```yaml
Instance Class: db.t3.micro (dev) / db.t3.small (prod)
Engine: MySQL 8.0
Multi-AZ: Enabled (production)
Backup Retention: 7 days
Backup Window: 03:00-04:00 UTC
Maintenance Window: Sun 04:00-05:00 UTC
Encryption: Enabled
Performance Insights: Enabled
```

### 📊 Monitoring & Logging

#### **CloudWatch Dashboards**
- Application performance metrics
- Database performance  
- Auto Scaling activity
- Error rates and response times

#### **Log Groups**
```bash
/aws/ec2/reserveease/application    # Application logs
/aws/rds/reserveease/error         # Database error logs  
/aws/lambda/edge                   # CloudFront edge logs
```

#### **Alarms**
- High CPU utilization (>80%)
- Database connection errors
- Application response time >2s
- High error rate (>5%)

### 🔧 Maintenance & Updates

#### **Backend Updates**
```bash
# Deploy new version
./deploy-backend.sh

# Rolling update (zero downtime)
aws autoscaling start-instance-refresh \
  --auto-scaling-group-name reserveease-backend-asg
```

#### **Frontend Updates**
```bash
# Deploy new version
./deploy-frontend.sh

# Invalidate CloudFront cache
aws cloudfront create-invalidation \
  --distribution-id E1234567890 \
  --paths "/*"
```

#### **Database Maintenance**
```bash
# Create manual snapshot
aws rds create-db-snapshot \
  --db-instance-identifier reserveease-db \
  --db-snapshot-identifier reserveease-backup-$(date +%Y%m%d)

# Monitor performance
aws rds describe-db-instances \
  --db-instance-identifier reserveease-db
```

### 🚨 Troubleshooting

#### **Common Issues**

**Backend not responding:**
```bash
# Check EC2 instance health
aws elbv2 describe-target-health \
  --target-group-arn arn:aws:elasticloadbalancing:...

# View application logs  
aws logs tail /aws/ec2/reserveease/application --follow
```

**Frontend not loading:**
```bash
# Check S3 bucket contents
aws s3 ls s3://reserveease-frontend-bucket/

# Check CloudFront distribution
aws cloudfront get-distribution --id E1234567890
```

**Database connectivity issues:**
```bash
# Test database connection
mysql -h reserveease-db.xxxx.region.rds.amazonaws.com \
      -u reserveease_admin -p reserveease

# Check RDS security groups
aws rds describe-db-instances \
  --db-instance-identifier reserveease-db \
  --query 'DBInstances[0].VpcSecurityGroups'
```

#### **Performance Optimization**

**Scale backend instances:**
```bash
# Update Auto Scaling capacity
aws autoscaling update-auto-scaling-group \
  --auto-scaling-group-name reserveease-backend-asg \
  --desired-capacity 3 \
  --max-size 5
```

**Optimize database:**
```bash
# Enable Performance Insights
aws rds modify-db-instance \
  --db-instance-identifier reserveease-db \
  --enable-performance-insights \
  --performance-insights-retention-period 7
```

### 🔄 Disaster Recovery

#### **Backup Strategy**
- **Database**: Automated daily backups with 7-day retention
- **Application Code**: Versioned in Git repositories
- **Infrastructure**: Terraform state backed up to S3
- **Static Assets**: S3 versioning enabled

#### **Recovery Procedures**
```bash
# Restore database from backup
aws rds restore-db-instance-from-db-snapshot \
  --db-instance-identifier reserveease-db-restored \
  --db-snapshot-identifier reserveease-backup-20231201

# Redeploy infrastructure
terraform apply -auto-approve

# Redeploy applications
./deploy-backend.sh && ./deploy-frontend.sh
```

### 📈 Scaling Guidelines

| Users | Instances | Database | Cost/Month |
|-------|-----------|----------|------------|
| 0-100 | 1x t3.micro | db.t3.micro | $25-35 |
| 100-1K | 2x t3.small | db.t3.small | $75-100 |
| 1K-10K | 3x t3.medium | db.t3.medium | $200-300 |
| 10K+ | 5+ t3.large | db.r5.large | $500+ |

### 🛠️ Advanced Configuration

#### **Custom Domain Setup**
```bash
# Update Terraform with your domain
terraform apply -var="domain_name=yourdomain.com"

# Configure DNS in Route 53 or your registrar
```

#### **Enhanced Security**
```bash
# Enable WAF protection
aws wafv2 create-web-acl --name ReserveEase-WAF

# Configure VPC Flow Logs
aws ec2 create-flow-logs --resources vpc-123456
```

#### **Multi-Region Setup**
```bash
# Deploy to additional regions
terraform workspace new us-west-2
terraform apply -var="aws_region=us-west-2"
```

---

## 🔧 Configuration

### Environment Variables

#### Backend
- `DB_HOST` - Database hostname
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `SES_USERNAME` - AWS SES username
- `SES_PASSWORD` - AWS SES password

#### Frontend
- `REACT_APP_API_URL` - Backend API URL

## 📊 Monitoring

The application includes comprehensive monitoring:

- **Health Checks**: Spring Boot Actuator endpoints
- **Metrics**: Custom CloudWatch metrics
- **Logs**: Centralized logging with CloudWatch
- **Alerting**: CloudWatch alarms for key metrics

## 🔒 Security

ReserveEase implements comprehensive security measures:

- **JWT-based authentication** - Secure token-based auth
- **SQL Injection Protection** - Multi-layer defense with JPA parameterized queries
- **Input Validation** - Custom validators with regex patterns and sanitization  
- **Request Filtering** - Real-time blocking of malicious requests
- **Rate Limiting** - 100 requests/minute per IP protection
- **Security Headers** - XSS protection, content type validation, CSP
- **HTTPS Enforced** - SSL/TLS encryption required
- **CORS Configuration** - Cross-origin request protection

See [SECURITY.md](SECURITY.md) for detailed security documentation.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

- **Email**: admin@reserveease.com
- **Website**: https://reserveease.com
- **AWS Documentation**: See deployment scripts in `/scripts/`
- **Infrastructure Code**: Terraform configurations in `/infrastructure/`

## 🎉 Acknowledgments

- Built with love for the open-source community
- Thanks to all contributors and users

---

**ReserveEase** - Making reservations easy, transparent, and free! 🏖️
