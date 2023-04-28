#!/bin/bash

# install database, upload artifact and setup service on AMI

# install mariadb 10.5/mysql 8.0 client
sudo amazon-linux-extras enable mariadb10.5
sudo yum clean metadata

echo $'y\n' | sudo yum install mariadb

sudo systemctl start mariadb
sudo systemctl able mariadb

echo $'\n n\n Y\n MyPassword11\n Liruolin@11\n Y\n Y\n Y\n Y\n' | sudo mysql_secure_installation

#install OpenJDK 17
wget https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_linux-x64_bin.tar.gz
tar xvf openjdk-17_linux-x64_bin.tar.gz
sudo mv jdk-17 /opt/

sudo tee /etc/profile.d/jdk.sh <<EOF
export JAVA_HOME=/opt/jdk-17
export PATH=\$PATH:\$JAVA_HOME/bin
EOF

source /etc/profile.d/jdk.sh

# upload and move artifact
sudo cp /tmp/webapp-0.0.1-SNAPSHOT.jar /etc/systemd/system/
sudo cp /tmp/webapp.service /etc/systemd/system/

# setup systemd service file
sudo chmod +x /etc/systemd/system/webapp.service
sudo systemctl daemon-reload

sudo systemctl start webapp.service
sudo systemctl enable webapp.service
ps -ef | grep "webapp"| grep -v grep

sudo systemctl status webapp.service -l