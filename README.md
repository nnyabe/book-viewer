# Book Viewer Application

## Overview

This project deploys a containerized book viewer application on AWS using a **Blue-Green deployment strategy**. The application is hosted in **Amazon ECS (Elastic Container Service) with Fargate**, behind an **Application Load Balancer**, and uses **CodeDeploy** for automated Blue-Green deployments.

## Architecture

### Infrastructure includes:

- **Networking:** VPC with public subnets across multiple availability zones
- **Compute:** ECS Fargate for containerized deployment
- **Load Balancing:** Application Load Balancer with Blue and Green target groups
- **CI/CD:** GitHub Actions for CI/CD pipeline and AWS CodeDeploy for Blue-Green deployments
- **Auto Scaling:** Application Auto Scaling for the ECS service based on CPU utilization
- **Security:** IAM roles with least privilege access for task execution and S3 access
- **Logging:** CloudWatch Logs for container logs

## Prerequisites

- AWS Account with appropriate permissions
- GitHub repository with proper secrets configuration
- Docker and Java development environment
- Maven for building the Java application

## AWS Resources

The **CloudFormation template** (`cloudformation.yaml`) provisions the following resources:

### VPC & Networking

- VPC with CIDR block `10.0.0.0/16`
- Two public subnets in different availability zones
- Internet Gateway and route tables

### ECS Resources

- ECS Cluster named `BookViewerCluster`
- Task Definition with **2048 CPU units** and **4096MB memory**
- ECS Service with **Blue-Green deployment** configuration

### Load Balancing

- **Application Load Balancer**
- **Two target groups** (Blue and Green)
- **Production listener** on port **80**
- **Test listener** on port **8081** for validation testing

### Security Groups

- Load Balancer Security Group allowing **HTTP, HTTPS, and application ports**
- ECS Service Security Group allowing **inbound traffic from the Load Balancer**

### IAM Roles

- **Task Execution Role** with permissions to pull container images and write logs
- **S3 Access Role** for the container to access S3 resources
- **CodeDeploy Service Role** for Blue-Green deployments
- **Auto Scaling Role** for ECS service scaling

### Auto Scaling

- **Target tracking scaling policy** based on CPU utilization
- **Min capacity:** 1, **Max capacity:** 3

### CodeDeploy

- Application and Deployment Group for ECS **Blue-Green deployments**

## CI/CD Pipeline

The **GitHub Actions** workflow (`.github/workflows/build-deploy.yml`) automates:

### Build Stage

1. Checks out the code
2. Sets up **JDK 21**
3. Builds the Java application using **Maven**
4. Builds and tags a **Docker image**
5. Pushes the image to **Amazon ECR Public**

### Deploy Stage

1. Gets the current **task definition**
2. Updates the container image in the **task definition**
3. Registers a new **task definition version**
4. Creates an **AppSpec file** for CodeDeploy
5. Starts a **Blue-Green deployment** using CodeDeploy

## Development Setup

### Local Development

#### Clone the repository:

```bash
git clone https://github.com/yourusername/book-viewer.git
cd book-viewer
```

#### Build the application:

```bash
mvn clean package
```

#### Build and run Docker container locally:

```bash
docker build -t book-viewer-repo .
docker run -p 8080:8080 book-viewer-repo
```

Access the application at `http://localhost:8080`

## Repository Configuration

Configure the following **GitHub repository secrets**:

- `AWS_ACCESS_KEY_ID`: AWS access key with appropriate permissions
- `AWS_SECRET_ACCESS_KEY`: AWS secret key corresponding to the access key

## Deployment

### Manual Deployment

#### Set up the AWS infrastructure:

```bash
aws cloudformation create-stack --stack-name book-viewer-stack --template-body file://cloudformation.yaml --capabilities CAPABILITY_IAM
```

#### Push to the master branch to trigger automatic deployment:

```bash
git push origin master
```

### Automatic Deployment

The **CI/CD pipeline** is triggered automatically when changes are pushed to the `master` branch. The workflow:

1. Builds and pushes a new container image to **ECR**
2. Updates the **ECS task definition** with the new image
3. Initiates a **Blue-Green deployment** using **CodeDeploy**

## Monitoring and Troubleshooting

### Logs

Application logs are available in **CloudWatch Logs**:

- **Log Group:** `book-viewer-log-group`
- **Log Stream Prefix:** `ecs`

### Monitoring Deployments

#### Monitor CodeDeploy deployments:

```bash
aws deploy get-deployment --deployment-id <DEPLOYMENT_ID>
```

#### Check ECS service status:

```bash
aws ecs describe-services --cluster BookViewerCluster --services book-viewer-service
```

### Monitor the application through the AWS Console:

- **ECS console** for container deployment status
- **CodeDeploy console** for deployment progress
- **CloudWatch** for logs and metrics

## Application Details

This **Java-based web application** serves as a book viewer, allowing users to read and navigate through digital books. The application is containerized and runs on **port 8080**.

## Security Considerations

- Uses **IAM roles** with least privilege access
- Security groups restrict traffic to only necessary ports
- Public subnets are used but with **restricted security group access**
- The **ECR repository** is publicly accessible (consider using a private repository for sensitive applications)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

[Specify your license information here]

