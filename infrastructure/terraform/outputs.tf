# Output values
output "vpc_id" {
  description = "ID of the VPC"
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "IDs of the public subnets"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "IDs of the private subnets"
  value       = aws_subnet.private[*].id
}

output "database_endpoint" {
  description = "RDS instance endpoint"
  value       = aws_db_instance.main.endpoint
  sensitive   = true
}

output "database_name" {
  description = "Database name"
  value       = aws_db_instance.main.db_name
}

output "load_balancer_dns" {
  description = "DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "load_balancer_zone_id" {
  description = "Hosted zone ID of the load balancer"
  value       = aws_lb.main.zone_id
}

output "cloudfront_distribution_id" {
  description = "CloudFront Distribution ID"
  value       = aws_cloudfront_distribution.main.id
}

output "cloudfront_domain_name" {
  description = "CloudFront Distribution Domain Name"
  value       = aws_cloudfront_distribution.main.domain_name
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket for frontend"
  value       = aws_s3_bucket.frontend.id
}

output "certificate_arn" {
  description = "ARN of the ACM certificate"
  value       = aws_acm_certificate.main.arn
}

output "application_url" {
  description = "Application URL"
  value       = "https://${var.domain_name}"
}

# Instructions for next steps
output "setup_instructions" {
  description = "Next steps to complete the setup"
  value = <<EOT
Next steps to complete your ReserveEase deployment:

1. DNS Configuration:
   - Create a CNAME record pointing ${var.domain_name} to ${aws_cloudfront_distribution.main.domain_name}
   - Create a CNAME record pointing www.${var.domain_name} to ${aws_cloudfront_distribution.main.domain_name}

2. SSL Certificate Validation:
   - Validate the ACM certificate by adding the DNS records shown in AWS Console

3. Backend Deployment:
   - Build your Spring Boot application JAR
   - Upload it to S3 or use CodeDeploy to deploy to EC2 instances
   - Update the Auto Scaling Group to deploy the latest version

4. Frontend Deployment:
   - Build your React application
   - Upload the build files to S3 bucket: ${aws_s3_bucket.frontend.id}
   - Invalidate CloudFront cache: aws cloudfront create-invalidation --distribution-id ${aws_cloudfront_distribution.main.id} --paths "/*"

5. Database Setup:
   - Connect to RDS instance: ${aws_db_instance.main.endpoint}
   - Run any necessary database migrations

6. Email Configuration:
   - Verify email addresses in AWS SES for sending reservation confirmations

Your infrastructure is now ready for ReserveEase!
EOT
}
