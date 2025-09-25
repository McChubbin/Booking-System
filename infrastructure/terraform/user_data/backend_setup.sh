#!/bin/bash

# Update system
yum update -y

# Install Java 17
yum install -y java-17-amazon-corretto-headless

# Install CloudWatch agent
yum install -y amazon-cloudwatch-agent

# Create application user
useradd -r -s /bin/false reserveease
mkdir -p /opt/reserveease
chown reserveease:reserveease /opt/reserveease

# Create application directory
mkdir -p /var/log/reserveease
chown reserveease:reserveease /var/log/reserveease

# Download and install application (placeholder - you'll need to build and upload your JAR)
cd /opt/reserveease

# Create environment file
cat > /opt/reserveease/.env << EOL
DB_HOST=${db_host}
DB_NAME=${db_name}
DB_USERNAME=${db_username}
DB_PASSWORD=${db_password}
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
EOL

chown reserveease:reserveease /opt/reserveease/.env
chmod 600 /opt/reserveease/.env

# Create systemd service file
cat > /etc/systemd/system/reserveease.service << EOL
[Unit]
Description=ReserveEase Backend Application
After=network.target

[Service]
Type=simple
User=reserveease
Group=reserveease
WorkingDirectory=/opt/reserveease
EnvironmentFile=/opt/reserveease/.env
ExecStart=/usr/bin/java -jar /opt/reserveease/reserveease-backend.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=reserveease

[Install]
WantedBy=multi-user.target
EOL

# Create CloudWatch agent config
cat > /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json << EOL
{
    "logs": {
        "logs_collected": {
            "files": {
                "collect_list": [
                    {
                        "file_path": "/var/log/reserveease/application.log",
                        "log_group_name": "/aws/ec2/reserveease/application",
                        "log_stream_name": "{instance_id}"
                    }
                ]
            }
        }
    },
    "metrics": {
        "namespace": "ReserveEase/Application",
        "metrics_collected": {
            "cpu": {
                "measurement": [
                    "cpu_usage_idle",
                    "cpu_usage_iowait",
                    "cpu_usage_user",
                    "cpu_usage_system"
                ],
                "metrics_collection_interval": 60
            },
            "disk": {
                "measurement": [
                    "used_percent"
                ],
                "metrics_collection_interval": 60,
                "resources": [
                    "*"
                ]
            },
            "diskio": {
                "measurement": [
                    "io_time"
                ],
                "metrics_collection_interval": 60,
                "resources": [
                    "*"
                ]
            },
            "mem": {
                "measurement": [
                    "mem_used_percent"
                ],
                "metrics_collection_interval": 60
            }
        }
    }
}
EOL

# Start and enable CloudWatch agent
/opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json -s
systemctl enable amazon-cloudwatch-agent
systemctl start amazon-cloudwatch-agent

# Note: The JAR file needs to be deployed separately
# You can use AWS CodeDeploy, S3, or other deployment methods

# Enable and start the service (will fail until JAR is deployed)
systemctl daemon-reload
systemctl enable reserveease

# Create a simple health check endpoint until the real app is deployed
echo "Service configured. Deploy the JAR file to /opt/reserveease/reserveease-backend.jar to start the application."
