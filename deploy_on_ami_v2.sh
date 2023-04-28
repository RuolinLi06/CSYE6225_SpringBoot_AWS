#!/bin/bash


sudo yum update
# install mariadb 10.5/mysql 8.0 client
sudo amazon-linux-extras enable mariadb10.5
sudo yum clean metadata

echo $'y\n' | sudo yum install mariadb

sudo systemctl stop mariadb
sudo systemctl disable mariadb

#Install OpenJDK 17
wget https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_linux-x64_bin.tar.gz
tar xvf openjdk-17_linux-x64_bin.tar.gz
sudo mv jdk-17 /opt/

sudo tee /etc/profile.d/jdk.sh <<EOF
export JAVA_HOME=/opt/jdk-17
export PATH=\$PATH:\$JAVA_HOME/bin
EOF

source /etc/profile.d/jdk.sh

sudo cp /tmp/webapp-0.0.1-SNAPSHOT.jar /home/ec2-user/
sudo cp /tmp/cloudwatch_config.json /opt/
sudo chmod +x /opt/cloudwatch_config.json

sudo systemctl daemon-reload

# install aws cloudwatch agent
cd ~
sudo wget https://s3.ca-central-1.amazonaws.com/amazoncloudwatch-agent-ca-central-1/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
chmod +x ./amazon-cloudwatch-agent.rpm
sudo rpm -U ./amazon-cloudwatch-agent.rpm
