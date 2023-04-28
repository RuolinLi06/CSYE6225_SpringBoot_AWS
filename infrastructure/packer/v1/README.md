## Building Custom Application AMI using Packer
Use Amazon Linux 2 as the source image for building AMI.

Use the shell provisioner to do the following:
- Upgrade the OS packages.
- Install all the application prerequisites, middleware, and runtime.
- Install MySQL(MariaDB).
- Update permission and file ownership on the copied application artifacts.

Use the file provisioner to do the following:
- Copy the application artifact to the right location.
- Copy the application configuration file to the right location.

## CI/CD
Github Action 

web_app_build_validate.yml:
AMI template is validated in the pull request status check.

packer_ami_build.yml:
AMI is built when PR is merged.

