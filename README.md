# WebApp 6255 Network Structrues & Cloud Computing

## Description
Build RestAPIs on SpringBoot3 with Spring Data JPA and MySql. APIs are Secured by Basic Authentication and Token with Spring Security5 and tested with JUnit. Implement CI/CD with Github Action and AWS. Implement Image Operation with AWS S3 Bucket. Use AWS CloudWatch to centralize the log.
Use StatsD to custom Metrics for API utilization.
## Requirements
### Environment and Technology
|Name  |Version  |
|--|--|
|Java  | 17 |
|Maven | 4 |
|SpringBoot  |3  |
|SpringSecurity|5 |
|Spring Data JPA  |  |
|MySQL /MariaDB | 8/ 10.5 |
|Lombok |  |
|Hibernator-Validator |  |
|log4j2||
|AWS SDK for Java|1.11.931|
|StatsD|3.1.0|

Use SpringBoot DataSource to auto-configurate database 
# CI/CD
Deploy on Amazon Linux2 AMI EC2 Build with Packer and Terraform.
Use Systemd to auto-run application service

## CI/CD with Github Action
web_app.yml:
- At pull_request to Main
- Run Maven Test
- Validate AMI template

packer_ami_build.yml: 
- After pull request merge
- Build Artifact
- Build AMI
- Create new AWS launch template
- Update AWS Auto Scaling Group
- Refresh instance



