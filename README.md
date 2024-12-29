# Enterprise Application Development Project

## Overview
This enterprise-level application implements a robust architecture using microservices, with both web and mobile clients. The system utilizes modern DevOps practices including CI/CD pipeline, containerization, and comprehensive testing.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Architecture](#architecture)
- [CI/CD Pipeline](#cicd-pipeline)
- [Project Structure](#project-structure)
- [Docker Configuration](#docker-configuration)
- [Getting Started](#getting-started)
- [Deployment](#deployment)

## Prerequisites

### Development Environment
- Java JDK 11 or higher
- Node.js 14.x or higher
- Android Studio 4.x or higher
- Git
- Docker and Docker Compose
- Maven 3.6.x or higher

### Infrastructure Requirements
- Jenkins server (port 8080)
- SonarQube server (port 9000)
- Nexus Repository (ports 5001-5003)
- Docker Registry
- Ubuntu servers for test/prod environments

## Architecture

### Global Architecture
```
[Place architecture diagram here]

The system consists of three main components:
- Web Client (React-based frontend)
- Mobile Client (Native Android application)
- Backend Services (Microservices architecture)
```

### CI/CD Pipeline Architecture
```
[Place CI/CD pipeline diagram here]

Pipeline workflow:
1. Developer pushes code to GitHub
2. Jenkins triggers build process
3. Maven builds and runs unit tests
4. SonarQube performs code analysis
5. Docker images are built and pushed
6. Deployment to test/prod environments
```

## Project Structure

```
├── backend/
│   ├── microservices/
│   ├── config/
│   └── docker-compose-local.yml
├── web-client/
│   ├── src/
│   └── Dockerfile
├── mobile-client/
│   ├── app/
│   └── Dockerfile
└── jenkins/
    └── Jenkinsfile
```

## CI/CD Pipeline

### Jenkins Pipeline Stages
1. **Source Code Management**
   - GitHub repository integration
   - Branch strategy: main and develop

2. **Build & Test**
   - Maven build
   - Unit testing
   - Integration testing

3. **Code Quality**
   - SonarQube analysis (port 9000)
   - Code coverage reports
   - Quality gates

4. **Artifact Management**
   - Nexus Repository Manager
   - Docker image storage
   - Version management

5. **Deployment**
   - Test server (192.168.1.26)
   - Production server (192.168.1.66)

## Docker Configuration

### Images
```yaml
# Backend Services
backend-service:
  image: backend-service:latest
  ports:
    - "8080:8080"

# Database
database:
  image: mysql:5.7
  environment:
    MYSQL_ROOT_PASSWORD: root
    MYSQL_DATABASE: app_db

# Web Client
web-client:
  image: web-client:latest
  ports:
    - "80:80"
```

## Getting Started

### Backend Setup
```bash
# Clone repository
git clone <repository-url>

# Navigate to backend directory
cd backend

# Start local environment
docker-compose -f docker-compose-local.yml up -d
```

### Web Client Setup
```bash
# Navigate to web client directory
cd web-client

# Install dependencies
npm install

# Start development server
npm start
```

### Mobile Client Setup
```bash
# Open project in Android Studio
# Configure gradle settings
# Build and run on emulator/device
```

## Deployment

### Test Environment
- Server: 192.168.1.26
- Docker configuration
- Automated deployment via Jenkins

### Production Environment
- Server: 192.168.1.66
- Production-grade Docker configuration
- Manual approval process
- Rollback procedures

## Environment Configuration

### Development
```bash
# Local development uses docker-compose
docker-compose -f docker-compose-local.yml up -d
```

### Testing
```bash
# Test environment deployment
docker stack deploy -c docker-compose.test.yml app_stack
```

### Production
```bash
# Production deployment
docker stack deploy -c docker-compose.prod.yml app_stack
```

## Monitoring and Logging
- Jenkins build monitoring
- SonarQube quality metrics
- Docker container logs
- Application-level logging

## Security Considerations
- HTTPS enforcement
- JWT authentication
- Docker security best practices
- Network segmentation

## Troubleshooting
- Common issues and solutions
- Log analysis procedures
- Contact information for support

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details
