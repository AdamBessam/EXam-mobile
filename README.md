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

### Infrastructure Requirements
- Jenkins server (port 8080)
- SonarQube server (port 9000)
- Nexus Repository (ports 5001-5003)
- Docker Registry
- Ubuntu servers for test/prod environments

## Architecture

### Global Architecture
![WhatsApp Image 2024-12-29 à 18 03 18_2e98a7d2](https://github.com/user-attachments/assets/ad549dcd-e99f-4694-b184-9043b96e234b)
```
The system consists of three main components:
- Web Client (React-based frontend)
- Mobile Client (Native Android application)
- Backend Services (Microservices architecture)
```

### CI/CD Pipeline Architecture
![WhatsApp Image 2024-12-11 à 10 51 22_4b07d7a4](https://github.com/user-attachments/assets/b8ea8086-605e-49fe-adef-c44e2c9c3972)
```



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
version: "3.8"
services:
  keycloak:
    image: quay.io/keycloak/keycloak:latest
    container_name: keycloak
    ports:
      - "8080:8080"
    environment:
      - KEYCLOAK_ADMIN=admin
      - KEYCLOAK_ADMIN_PASSWORD=admin
      - KC_HOSTNAME_PATH=/auth
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KC_HTTP_ENABLED=true
      - KC_HTTP_RELATIVE_PATH=/auth
      - KC_PROXY=edge
      - KC_PROXY_ADDRESS_FORWARDING=true
    command:
      - start-dev
      - --http-enabled=true
      - --hostname=localhost
    volumes:
      - ./Keycloak-Docker/data:/opt/keycloak/data

  mysql:
    image: mysql:8.0
    container_name: mysql-firstaid
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=first_aid_participant_bd
      - MYSQL_USER=user
      - MYSQL_PASSWORD=password
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docker/data/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"
    healthcheck:
      test: [ "CMD", "mysqladmin", "ping", "-h", "localhost" ]
      interval: 30s
      timeout: 10s
      retries: 5

  phpmyadmin:
    image: phpmyadmin/phpmyadmin
    container_name: phpmyadmin-firstaid
    ports:
      - "8089:80"
    environment:
      PMA_HOST: mysql
      PMA_PORT: 3306

  config-service:
    build:
      context: ./config-service
      dockerfile: Dockerfile
    container_name: config-service
    ports:
      - "9999:9999"
    depends_on:
      discovery-service:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-service:8761/eureka/
    healthcheck:
      test: [ "CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:9999/actuator/health" ]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s

  discovery-service:
    build:
      context: ./discovery-service
      dockerfile: Dockerfile
    container_name: discovery-service
    ports:
      - "8761:8761"
    healthcheck:
      test: [ "CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8761/actuator/health" ]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s

  gateway-service:
    build:
      context: ./gateway-service
      dockerfile: Dockerfile
    container_name: gateway-service
    ports:
      - "8888:8888"
    depends_on:
      keycloak:
        condition: service_started
      discovery-service:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-service:8761/eureka/

  participant-service:
    build:
      context: ./participant-service
      dockerfile: Dockerfile
    container_name: participant-service
    ports:
      - "8082:8082"
    depends_on:
      mysql:
        condition: service_healthy
      discovery-service:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-service:8761/eureka/
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/first_aid_participant_bd
      - SPRING_DATASOURCE_USERNAME=user
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
      - SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

  training-service:
    build:
      context: ./training-service
      dockerfile: Dockerfile
    container_name: training-service
    ports:
      - "8081:8081"
    depends_on:
      mysql:
        condition: service_healthy
      discovery-service:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-service:8761/eureka/
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/first_aid_training_bd
      - SPRING_DATASOURCE_USERNAME=user
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
      - SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

  loki:
    image: grafana/loki:main
    command: [ '-config.file=/etc/loki/local-config.yaml' ]
    ports:
      - '3100:3100'

  prometheus:
    image: prom/prometheus:v2.46.0
    container_name: prometheus
    command:
      - --enable-feature=exemplar-storage
      - --config.file=/etc/prometheus/prometheus.yml
    volumes:
      - ./docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
    ports:
      - "9090:9090"

  tempo:
    image: grafana/tempo:2.2.2
    container_name: tempo
    command: [ "-config.file=/etc/tempo.yaml" ]
    volumes:
      - ./docker/tempo/tempo.yml:/etc/tempo.yaml:ro
      - ./data/tempo:/tmp/tempo
    ports:
      - "3110:3100"
      - "9411:9411"

  grafana:
    image: grafana/grafana:10.1.0
    container_name: grafana
    volumes:
      - ./docker/grafana:/etc/grafana/provisioning/datasources:ro
    environment:
      - GF_AUTH_ANONYMOUS_ENABLED=true
      - GF_AUTH_ANONYMOUS_ORG_ROLE=Admin
      - GF_AUTH_DISABLE_LOGIN_FORM=true
    ports:
      - "3000:3000"

volumes:
  mysql_data:

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



## Contributing


## License
This project is licensed under the MIT License - see the LICENSE.md file for details
