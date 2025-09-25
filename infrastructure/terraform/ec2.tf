# Key Pair for EC2 instances
resource "aws_key_pair" "main" {
  key_name   = "${var.app_name}-keypair"
  public_key = file("~/.ssh/id_rsa.pub") # You'll need to generate this

  tags = {
    Name        = "${var.app_name}-keypair"
    Environment = var.environment
  }
}

# Launch Template for Backend Application
resource "aws_launch_template" "backend" {
  name_prefix   = "${var.app_name}-backend-"
  image_id      = "ami-0c02fb55956c7d316" # Amazon Linux 2023
  instance_type = "t3.micro"
  key_name      = aws_key_pair.main.key_name

  vpc_security_group_ids = [aws_security_group.app.id]

  iam_instance_profile {
    name = aws_iam_instance_profile.ec2_profile.name
  }

  user_data = base64encode(templatefile("${path.module}/user_data/backend_setup.sh", {
    db_host     = aws_db_instance.main.endpoint
    db_name     = aws_db_instance.main.db_name
    db_username = var.db_username
    db_password = var.db_password
    app_name    = var.app_name
  }))

  tag_specifications {
    resource_type = "instance"
    tags = {
      Name        = "${var.app_name}-backend"
      Environment = var.environment
      Type        = "Backend"
    }
  }

  lifecycle {
    create_before_destroy = true
  }
}

# Auto Scaling Group for Backend
resource "aws_autoscaling_group" "backend" {
  name                = "${var.app_name}-backend-asg"
  vpc_zone_identifier = aws_subnet.private[*].id
  target_group_arns   = [aws_lb_target_group.backend.arn]
  health_check_type   = "ELB"
  health_check_grace_period = 300

  min_size         = 1
  max_size         = 3
  desired_capacity = 2

  launch_template {
    id      = aws_launch_template.backend.id
    version = "$Latest"
  }

  tag {
    key                 = "Name"
    value               = "${var.app_name}-backend-asg"
    propagate_at_launch = false
  }

  tag {
    key                 = "Environment"
    value               = var.environment
    propagate_at_launch = true
  }
}

# IAM Role for EC2 instances
resource "aws_iam_role" "ec2_role" {
  name = "${var.app_name}-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name        = "${var.app_name}-ec2-role"
    Environment = var.environment
  }
}

# IAM Policy for EC2 instances
resource "aws_iam_role_policy" "ec2_policy" {
  name = "${var.app_name}-ec2-policy"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:DescribeLogStreams"
        ]
        Resource = "arn:aws:logs:*:*:*"
      },
      {
        Effect = "Allow"
        Action = [
          "ses:SendEmail",
          "ses:SendRawEmail"
        ]
        Resource = "*"
      }
    ]
  })
}

# IAM Instance Profile
resource "aws_iam_instance_profile" "ec2_profile" {
  name = "${var.app_name}-ec2-profile"
  role = aws_iam_role.ec2_role.name

  tags = {
    Name        = "${var.app_name}-ec2-profile"
    Environment = var.environment
  }
}
