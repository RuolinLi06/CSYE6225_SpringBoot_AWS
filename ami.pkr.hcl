// export PKR_VAR_aws_access_key=$YOURKEY
// export PKR_VAR_aws_secret_key=$YOURSECRETKEY


variable "aws_region" {
  type    = string
  default = "ca-central-1"
}

variable "source_ami" {
  type    = string
  default = "ami-099effcf516c942b7" # Amazon Linux 2
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "subnet_id" {
  type    = string
  default = "subnet-063efa1e325209e97"
}

variable "file_name" {
  type = string
  default = "webapp-0.0.1-SNAPSHOT"
}



# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  ami_regions = [
    "ca-central-1",
  ]
  ami_users =["891111483651"]
  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }
  // aws_polling {
  //   delay_seconds = 40
  //   max_attempts  = 15
  // }


  instance_type = "t2.micro"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"

  launch_block_device_mappings {
    delete_on_termination = true
    // device_name           = "/dev/sdf"
    device_name           = "/dev/xvda"
    volume_size           = 8
    volume_type           = "gp2"
  }
}


build {
  sources = ["source.amazon-ebs.my-ami"]

  provisioner "file" {
    source      = "${var.file_name}.jar"
    destination = "/tmp/${var.file_name}.jar"
  }

  provisioner "file" {
      source      = "infrastructure/aws/cloudwatch/cloudwatch_config.json"
      destination = "/tmp/cloudwatch_config.json"
  }

  provisioner "shell" { 
    script = "deploy_on_ami_v2.sh"

  } 
}