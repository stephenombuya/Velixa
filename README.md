# Velixa Application

## Overview

Velixa is a modern, scalable microservices application built with Spring Boot, MongoDB, and HTML/CSS/JavaScript. This project implements a robust architecture designed for high availability, resilience, and security while providing a seamless user experience.

## Technology Stack

### Backend
- **Java**: Core programming language
- **Spring Boot**: Application framework
- **Spring Cloud**: Microservices ecosystem components
- **MongoDB**: NoSQL database for data persistence
- **Spring Security**: Authentication and authorization
- **JWT**: Token-based user authentication
- **SpringDoc OpenAPI**: API documentation

### Frontend
- **HTML5**: Markup language
- **CSS3**: Styling
- **JavaScript**: Frontend interactivity

### DevOps & Deployment
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **Maven**: Dependency management and build tool

## Project Architecture

Velixa follows a microservices architecture with the following components:

### Core Services
1. **API Gateway**: Entry point for all client requests, handles routing and cross-cutting concerns
2. **Auth Service**: Manages user authentication, authorization, and JWT token handling
3. **Service Registry**: Service discovery using Netflix Eureka
4. **Config Server**: Centralized configuration management
5. **Domain-specific microservices**: Business functionality divided into bounded contexts

### Cross-cutting Concerns
- **Security**: JWT-based authentication and fine-grained authorization
- **Rate Limiting**: Protection against abuse and DoS attacks
- **API Documentation**: Comprehensive API docs with OpenAPI/Swagger
- **Monitoring**: Health checks and metrics with Spring Boot Actuator

## Project Structure

```
Velixa/
├── docker-compose.yml                   # For orchestrating all services
├── README.md                            # Project documentation
├── backend/                             # Backend services directory
│   ├── api-gateway/                     # API Gateway service
│   ├── auth-service/                    # Authentication/Authorization service
│   ├── service-registry/                # Service discovery (Eureka)
│   ├── config-server/                   # Centralized configuration
│   ├── service-one/                     # First domain microservice
│   ├── service-two/                     # Second domain microservice
│   └── ...                              # Additional microservices
└── frontend/                            # Frontend application
    ├── public/                          # Static assets
    ├── src/                             # Source code
    ├── package.json                     # Frontend dependencies
    └── ...                              # Other frontend config files
```

## Backend Microservice Structure

Each microservice follows a standardized structure:

```
service-name/
├── Dockerfile                           # Service-specific Docker configuration
├── pom.xml                              # Maven dependencies
├── src/
│   ├── main/
│   │   ├── java/com/velixa/servicename/
│   │   │   ├── ServiceNameApplication.java  # Main application class
│   │   │   ├── config/                  # Configuration classes
│   │   │   │   └── SecurityConfig.java  # Security configuration
│   │   │   ├── controller/              # REST controllers
│   │   │   │   └── ApiController.java
│   │   │   ├── model/                   # Data models/entities
│   │   │   │   └── EntityName.java
│   │   │   ├── repository/              # MongoDB repositories
│   │   │   │   └── EntityRepository.java
│   │   │   ├── service/                 # Business logic layer
│   │   │   │   ├── ServiceInterface.java
│   │   │   │   └── ServiceImpl.java
│   │   │   ├── dto/                     # Data Transfer Objects
│   │   │   │   └── EntityDTO.java
│   │   │   ├── exception/               # Custom exceptions
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   └── CustomException.java
│   │   │   └── util/                    # Utility classes
│   │   │       └── Helper.java
│   │   └── resources/
│   │       ├── application.yml          # Application properties
│   │       ├── bootstrap.yml            # Config server connection (if used)
│   │       └── static/                  # Static resources if needed
│   └── test/
│       └── java/com/velixa/servicename/
│           ├── controller/              # Controller tests
│           ├── service/                 # Service layer tests
│           └── repository/              # Repository tests
└── README.md                            # Service-specific documentation
```

## Frontend Structure

```
velixa-frontend/
├── public/
│   ├── index.html
│   ├── favicon.ico
│   └── assets/
│       ├── images/
│       │   └── logo.svg
│       └── icons/
├── src/
│   ├── index.js
│   ├── css/
│   │   ├── main.css
│   │   └── components/
│   │       ├── auth.css
│   │       ├── dashboard.css
│   │       ├── navbar.css
│   │       └── forms.css
│   ├── js/
│   │   ├── api/
│   │   │   ├── apiClient.js
│   │   │   └── endpoints/
│   │   │       ├── authEndpoints.js
│   │   │       └── serviceEndpoints.js
│   │   ├── auth/
│   │   │   ├── auth.js
│   │   │   └── authStorage.js
│   │   ├── components/
│   │   │   ├── navbar.js
│   │   │   ├── sidebar.js
│   │   │   ├── notification.js
│   │   │   └── modal.js
│   │   ├── pages/
│   │   │   ├── home.js
│   │   │   ├── login.js
│   │   │   ├── register.js
│   │   │   ├── dashboard.js
│   │   │   └── profile.js
│   │   └── utils/
│   │       ├── validator.js
│   │       ├── formatter.js
│   │       └── errorHandler.js
│   └── templates/
│       ├── header.html
│       ├── footer.html
│       └── sidebar.html
├── package.json
└── README.md
```

## Development Setup

### Prerequisites
- Java JDK 17+
- Maven 3.8+
- Docker and Docker Compose
- MongoDB (or use the dockerized version)
- IDE (VS Code with Spring Boot extensions recommended, or Spring Tool Suite)

### Getting Started

1. **Clone the repository**
   ```
   git clone https://github.com/stephenombuya/Velixa
   cd Velixa
   ```

2. **Backend Setup**
   - Navigate to each microservice directory and build:
     ```
     cd backend/service-name
     mvn clean install
     ```

3. **Frontend Setup**
   - Install dependencies:
     ```
     cd frontend
     npm install
     ```

4. **Run with Docker Compose**
   ```
   docker-compose up -d
   ```

5. **Access the Application**
   - Frontend: http://localhost:8080
   - Swagger API Documentation: http://localhost:8080/swagger-ui.html
   - Eureka Dashboard: http://localhost:8761

## Key Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control
- Secure password hashing
- Token refresh mechanism

### API Gateway Features
- Request routing
- Load balancing
- CORS configuration
- Rate limiting
- JWT verification

### Security Implementation
- HTTPS endpoints
- Input validation
- Protection against common attacks (XSS, CSRF)
- API rate limiting
- Security headers

## Testing

Velixa uses a comprehensive testing strategy:

- **Unit Tests**: For individual components (services, repositories)
  ```
  mvn test
  ```

- **Integration Tests**: For testing interactions between components
  ```
  mvn verify -P integration-test
  ```

- **API Tests**: For testing REST endpoints
  ```
  mvn verify -P api-test
  ```

## Deployment

### Docker Deployment
Deploy the entire application stack using Docker Compose:
```
docker-compose -f docker-compose.yml up -d
```

For production deployment:
```
docker-compose -f docker-compose.prod.yml up -d
```

### Kubernetes Deployment
Kubernetes deployment configurations are available in the `deployment/k8s` directory.

## API Documentation

API documentation is generated using SpringDoc OpenAPI and is available at:
```
http://localhost:8080/swagger-ui.html
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the GNU General Public License  - see the `LICENSE` file for details.
