# PawPlanet Backend

A clean, production-ready Spring Boot backend application with PostgreSQL database, Flyway migrations, and comprehensive Swagger/OpenAPI documentation. Configured for Heroku deployment with DATABASE_URL support.

## Features

- **Spring Boot 3.2.1** - Modern Java framework
- **Java 17** - Latest LTS Java version
- **PostgreSQL** - Production-grade relational database
- **Flyway** - Database migration management
- **Swagger/OpenAPI 3.0** - Interactive API documentation
- **Clean Architecture** - Layered structure (Entity, Repository, Service, Controller)
- **RESTful API** - Well-designed REST endpoints
- **Validation** - Input validation with Bean Validation
- **Lombok** - Reduced boilerplate code
- **Heroku-Ready** - DATABASE_URL support with auto-conversion
- **Environment Variables** - .env file support for local development

## Architecture

```
com.pawpplanet.backend
├── config/          # Configuration (Swagger, DotenvConfig)
├── controller/      # REST API controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities (User, Pet, Post, etc.)
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic layer
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher (for local dev)

### Database Setup

#### Local Development

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your local PostgreSQL credentials:
   ```env
   DATABASE_URL=postgres://username:password@localhost:5432/pawplanet
   JWT_SECRET=your-local-secret
   ```

3. The application will automatically:
   - Load `.env` file via `DotenvConfig`
   - Convert `postgres://` format to JDBC format
   - Run Flyway migrations on startup

#### Heroku Deployment

Heroku automatically provides `DATABASE_URL` when you add PostgreSQL addon:
```bash
heroku addons:create heroku-postgresql:mini
```

No additional configuration needed - the app auto-converts Heroku's `DATABASE_URL` format!

### Build

```bash
mvn clean compile
```

### Run

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Test

```bash
mvn test
```

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
- `GET /api/health` - Application health status (returns `{"status":"UP","timestamp":"..."}`)

### Example Endpoints
- `GET /api/v1/pets` - Get all pets
- `GET /api/v1/pets/{id}` - Get pet by ID
- `POST /api/v1/pets` - Create a new pet
- `PUT /api/v1/pets/{id}` - Update a pet
- `DELETE /api/v1/pets/{id}` - Delete a pet

## Environment Configuration

### Required Environment Variables

- **`DATABASE_URL`** - PostgreSQL connection URL (Heroku format: `postgres://user:pass@host:port/db`)
- **`JWT_SECRET`** (optional) - Secret key for JWT authentication
- **`SPRING_PROFILES_ACTIVE`** (Heroku production) - Set to `prod` for optimized connection pool

### Connection Pool Management

**⚠️ IMPORTANT for Heroku Essential Plan (20 connections max)**

The application uses **HikariCP** with optimized settings:

#### Local Development (`application.yml`)
```yaml
hikari:
  maximum-pool-size: 5      # Safe for local development
  minimum-idle: 1
```

#### Production (`application-prod.yml`)
```yaml
hikari:
  maximum-pool-size: 2      # Optimized for Heroku Essential
  minimum-idle: 0           # No idle connections
  idle-timeout: 10000       # 10 seconds
  leak-detection-threshold: 2000  # Detect leaks early
```

**Set production profile on Heroku:**
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod --app your-app-name
```

**Monitor connection pool:**
```bash
# View HikariCP stats in logs
heroku logs --tail --app your-app-name | grep -i hikari

# Check database connections
heroku pg:ps --app your-app-name
```

➡️ **See [HEROKU-CONNECTION-FIX.md](HEROKU-CONNECTION-FIX.md) for detailed guide**

### Flyway Migrations

**🔧 Manual Migration (Recommended for Production)**

Flyway is **disabled** by default to prevent connection pool issues.

#### Local Development
```bash
# Run migrations manually
./scripts/flyway-migrate.ps1
# Or: mvn flyway:migrate
```

#### Heroku Production
```bash
# Run migration on Heroku (one-off dyno)
heroku run ./mvnw flyway:migrate --app your-app-name
```

**Why manual migration?**
- Avoids holding connections during app restart
- Better control over schema changes
- Safer for Heroku Essential plan (limited connections)

### Local Development with .env

The project uses `dotenv-java` to automatically load `.env` file:

1. Copy `.env.example` to `.env`
2. Update with your local values
3. Run `mvn spring-boot:run` - environment variables auto-loaded!

**Example `.env`:**
```env
DATABASE_URL=postgres://postgres:postgres@localhost:5432/pawplanet
JWT_SECRET=local-development-secret-key
```

### Heroku Configuration

Set Config Vars in Heroku Dashboard or via CLI:

```bash
# DATABASE_URL is auto-created when you add PostgreSQL addon
heroku addons:create heroku-postgresql:mini

# Set additional variables
heroku config:set JWT_SECRET=your-production-secret
```

## Deployment

### Heroku Deployment

#### Prerequisites
- Heroku CLI installed
- Git repository initialized

#### Deploy Steps

1. **Create Heroku app:**
   ```bash
   heroku create your-app-name
   ```

2. **Add PostgreSQL addon:**
   ```bash
   heroku addons:create heroku-postgresql:mini
   ```

3. **Set environment variables:**
   ```bash
   heroku config:set JWT_SECRET=your-secret-key
   ```

4. **Deploy:**
   ```bash
   git push heroku main
   ```

5. **Run migrations:**
   Flyway runs automatically on startup!

6. **Check logs:**
   ```bash
   heroku logs --tail
   ```

### GitHub Actions CI/CD

The project includes automated workflows:

- **CI (`ci.yml`)**: Runs tests on pull requests to `main` and `develop`
- **CD (`cd.yml`)**: Automatically deploys to Heroku on push to `main`

**Required GitHub Secrets:**
- `HEROKU_API_KEY` - Your Heroku API key
- `HEROKU_APP_NAME` - Your Heroku app name
- `HEROKU_EMAIL` - Your Heroku account email

## Example Request

```bash
# Health check
curl http://localhost:8080/api/health

# Create a new pet (example - endpoints not yet implemented)
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

### Core Framework
- **Spring Boot 3.2.1** - Application framework
- **Java 17** - LTS runtime

### Database
- **PostgreSQL** - Production database
- **Flyway** - Database migrations
- **Spring Data JPA** - ORM layer

### API & Documentation
- **Spring Web** - REST API support
- **Spring Validation** - Input validation
- **SpringDoc OpenAPI 3.0** - Swagger documentation

### Development Tools
- **Lombok** - Code generation
- **Spring DevTools** - Hot reload
- **dotenv-java** - Environment variable management

### Build & Deploy
- **Maven** - Build tool
- **GitHub Actions** - CI/CD pipelines
- **Heroku** - Cloud platform

## Project Structure

```
PawPlanet-backend/
├── .github/
│   └── workflows/
│       ├── ci.yml              # CI workflow (tests on PR)
│       └── cd.yml              # CD workflow (deploy to Heroku)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pawpplanet/backend/
│   │   │       ├── PawPlanetBackendApplication.java
│   │   │       ├── config/
│   │   │       │   ├── DotenvConfig.java          # .env loader
│   │   │       │   └── OpenApiConfig.java         # Swagger config
│   │   │       ├── controller/
│   │   │       │   └── HealthController.java      # Health check API
│   │   │       ├── dto/
│   │   │       ├── entity/                        # JPA entities
│   │   │       │   ├── user/
│   │   │       │   ├── pet/
│   │   │       │   ├── post/
│   │   │       │   ├── encyclopedia/
│   │   │       │   └── notification/
│   │   │       ├── repository/                    # Spring Data repos
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.yml                    # Spring config
│   │       ├── db/migration/                      # Flyway migrations
│   │       │   └── V1__init_schema.sql
│   │       └── META-INF/
│   │           └── spring.factories               # DotenvConfig registration
│   └── test/
│       └── java/
│           └── com/pawpplanet/backend/
│               └── controller/
│                   └── HealthControllerTest.java
├── .env                        # Local environment vars (NOT in git)
├── .env.example                # Environment template
├── .gitignore
├── pom.xml                     # Maven dependencies
├── system.properties           # Heroku Java version
├── Procfile                    # Heroku process definition
├── README.md
├── DOTENV-GUIDE.md            # .env usage guide
├── HEROKU-CONFIG.md           # Heroku configuration guide
└── DATABASE-URL-FIX-SUMMARY.md # Troubleshooting guide
```

## Important Files

### Configuration Files

- **`system.properties`** - Specifies Java 17 for Heroku
- **`Procfile`** - Heroku process definition (if needed)
- **`.env`** - Local environment variables (DO NOT commit)
- **`.env.example`** - Template for environment variables

### Database

- **`src/main/resources/db/migration/`** - Flyway SQL migrations
- Migrations run automatically on application startup

## Troubleshooting

### Common Issues

1. **"Failed to determine suitable jdbc url"**
   - Ensure `.env` file exists with `DATABASE_URL`
   - Run `mvn clean compile` to regenerate classes

2. **Flyway version conflicts**
   - Do NOT add `flyway-database-postgresql` dependency
   - Spring Boot manages Flyway versions automatically

3. **PostgreSQL driver not found**
   - Ensure `org.postgresql:postgresql` dependency is in `pom.xml`
   - Run `mvn clean install` to download dependencies

See `DATABASE-URL-FIX-SUMMARY.md` for detailed troubleshooting.

## Documentation

- **`README.md`** (this file) - Project overview and setup
- **`DOTENV-GUIDE.md`** - How to use .env for local development
- **`HEROKU-CONFIG.md`** - Heroku deployment configuration guide
- **`DATABASE-URL-FIX-SUMMARY.md`** - Common errors and solutions

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

CI will automatically run tests on your PR!

## License

MIT License