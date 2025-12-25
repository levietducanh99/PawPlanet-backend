# PawPlanet Backend

A clean, production-ready Spring Boot backend application with comprehensive Swagger/OpenAPI documentation.

## Features

- **Spring Boot 3.2.1** - Modern Java framework
- **Java 17** - Latest LTS Java version
- **Swagger/OpenAPI 3.0** - Interactive API documentation
- **Clean Architecture** - Layered structure (Controller, Service, Model, DTO)
- **RESTful API** - Well-designed REST endpoints
- **Validation** - Input validation with Bean Validation
- **Lombok** - Reduced boilerplate code

## Architecture

```
com.pawpplanet.backend
├── config/          # Configuration classes (Swagger, etc.)
├── controller/      # REST API controllers
├── dto/             # Data Transfer Objects
├── model/           # Domain models/entities
└── service/         # Business logic layer
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build

```bash
mvn clean compile
```

### Run

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Package

```bash
mvn clean package
```

## API Documentation

Once the application is running, access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Alternative URL**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## API Endpoints

### Health Check
- `GET /api/v1/health` - Application health status

### Pet Management
- `GET /api/v1/pets` - Get all pets
- `GET /api/v1/pets/{id}` - Get pet by ID
- `POST /api/v1/pets` - Create a new pet
- `PUT /api/v1/pets/{id}` - Update a pet
- `DELETE /api/v1/pets/{id}` - Delete a pet

## Example Request

```bash
# Create a new pet
curl -X POST http://localhost:8080/api/v1/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Buddy",
    "species": "Dog",
    "breed": "Golden Retriever",
    "age": 3,
    "description": "Friendly and playful"
  }'
```

## Technology Stack

- **Spring Boot** - Application framework
- **Spring Web** - REST API support
- **Spring Validation** - Input validation
- **SpringDoc OpenAPI** - Swagger documentation
- **Lombok** - Code generation
- **Maven** - Build tool

## Project Structure

```
PawPlanet-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pawpplanet/backend/
│   │   │       ├── PawPlanetBackendApplication.java
│   │   │       ├── config/
│   │   │       │   └── OpenApiConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── HealthController.java
│   │   │       │   └── PetController.java
│   │   │       ├── dto/
│   │   │       │   ├── ApiResponse.java
│   │   │       │   └── PetDto.java
│   │   │       ├── model/
│   │   │       │   └── Pet.java
│   │   │       └── service/
│   │   │           └── PetService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── .gitignore
├── pom.xml
└── README.md
```

## License 123cc

MIT License