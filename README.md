# Enterprise Microservices Platform

A complete, runnable enterprise microservices project skeleton using **Java 21, Spring Boot, Spring Cloud, and Maven**.

## Architecture

```
                    HTML API CONSOLE
                           |
                           v
                    API GATEWAY :8080
                           |
                           v
                 EUREKA :8761
                           |
            +--------------+--------------+
            |              |              |
            v              v              v
       USER :8081     PARKING :8082   PAYMENT :8083
```

### Request Flow

```
HTML API CONSOLE
       |
       v
API GATEWAY :8080
       |
       v
SERVICE DISCOVERY :8761
       |
       v
TARGET MICROSERVICE
       |
       v
DUMMY API / DATA
       |
       v
TARGET MICROSERVICE
       |
       v
API GATEWAY
       |
       v
HTML API CONSOLE
```

## Build Architecture

```
                 ROOT POM
                    |
                    v
                   BOM
                    |
        +-----------+-----------+
        |           |           |
      Common    Infrastructure Services
```

### Why BOM?

The Bill of Materials (BOM) centrally manages all third-party dependency versions. Individual services MUST NOT define versions directly. This ensures:
- Consistent dependency versions across all microservices
- Easy version upgrades (change one place)
- No version conflicts between modules

### Why Gateway?

The API Gateway is the single external entry point. It provides:
- Request routing to microservices
- CORS handling
- Correlation ID propagation
- Request logging
- Error handling
- Placeholder for authentication/authorization

### Why Service Discovery?

Eureka Service Discovery allows services to find each other dynamically:
- Services register on startup
- Gateway routes using service names (not hardcoded IPs)
- Supports load balancing and failover

## Technology Stack

- Java 21
- Spring Boot 3.4.x
- Spring Cloud 2024.0.x
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Maven
- Lombok
- Jackson
- Bean Validation
- Spring Boot Actuator
- JUnit 5 + Mockito
- SLF4J / Logback
- HTML5 / CSS3 / Vanilla JavaScript

## Project Structure

```
enterprise-microservices-platform/
├── pom.xml                          # Root parent POM
├── bom/
│   └── pom.xml                      # Bill of Materials
├── common/
│   ├── common-core/                 # Constants, utilities, models
│   ├── common-web/                  # API response models
│   ├── common-exception/            # Global exception handling
│   └── common-logging/              # Correlation ID logging
├── infrastructure/
│   ├── api-gateway/                 # Spring Cloud Gateway :8080
│   └── service-discovery/           # Eureka Server :8761
├── services/
│   ├── user-service/                # User API :8081
│   ├── parking-service/             # Parking API :8082
│   └── payment-service/             # Payment API :8083
├── api-console/                     # HTML API testing console
├── docker/
│   └── docker-compose.yml           # Docker orchestration
└── README.md
```

## Quick Start (Local Development)

### Prerequisites

- Java 21
- Maven 3.9+
- (Optional) Docker & Docker Compose

### Step 1: Build All Modules

```bash
mvn clean install -DskipTests
```

### Step 2: Start Services (in order)

Open separate terminals and run:

```bash
# Terminal 1 - Service Discovery (start first)
cd infrastructure/service-discovery
mvn spring-boot:run

# Terminal 2 - API Gateway (wait for Eureka to start)
cd infrastructure/api-gateway
mvn spring-boot:run

# Terminal 3 - User Service
cd services/user-service
mvn spring-boot:run

# Terminal 4 - Parking Service
cd services/parking-service
mvn spring-boot:run

# Terminal 5 - Payment Service
cd services/payment-service
mvn spring-boot:run
```

Wait for Eureka dashboard to show all services registered at http://localhost:8761.

### Step 3: Open API Console

Open `api-console/index.html` in your browser.

### Step 4: Test the APIs

Click the quick API buttons or manually enter endpoints.

## Running with Docker

```bash
# Build all modules first
mvn clean install -DskipTests

# Start all services
cd docker
docker-compose up --build
```

## API Endpoints

### User Service (via Gateway)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1/users | Get all users |
| GET | /api/v1/users/{id} | Get user by ID |

### Parking Service (via Gateway)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1/parking/slots | Get all parking slots |
| GET | /api/v1/parking/slots/{id} | Get slot by ID |

### Payment Service (via Gateway)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1/payments | Get all payments |
| GET | /api/v1/payments/{id} | Get payment by ID |

### Health Endpoints

| Service | Endpoint |
|---------|----------|
| Gateway | http://localhost:8080/actuator/health |
| Discovery | http://localhost:8761/actuator/health |
| User | http://localhost:8081/actuator/health |
| Parking | http://localhost:8082/actuator/health |
| Payment | http://localhost:8083/actuator/health |

## Correlation ID Flow

Every request propagates `X-Correlation-ID` through the entire chain:

```
Browser sends: X-Correlation-ID: abc-123
    |
    v
Gateway receives abc-123 (or generates one if missing)
    |
    v
Target service logs with abc-123
    |
    v
Response includes abc-123
```

All services log using MDC with the correlation ID.

## Request Flow Visualization

The API Console visually tracks request lifecycle:

1. **CLIENT** - Request initiated
2. **API GATEWAY** - Route resolved
3. **SERVICE DISCOVERY** - Service instance located
4. **TARGET SERVICE** - Request processed
5. **RESPONSE** - Response returned

Status indicators:
- `○ WAITING` - Not yet processed
- `⟳ PROCESSING` - Currently handling
- `✓ COMPLETED` - Successfully done
- `✕ FAILED` - Error occurred

## How to Add a New Microservice

### 1. Create the module

```bash
mkdir -s services/notification-service/src/main/java/com/enterprise/platform/notification
```

### 2. Create pom.xml

```xml
<parent>
    <groupId>com.enterprise.platform</groupId>
    <artifactId>enterprise-microservices-platform</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <relativePath>../../pom.xml</relativePath>
</parent>
```

### 3. Dependencies (no versions needed - BOM handles it)

```xml
<dependency>
    <groupId>com.enterprise.platform</groupId>
    <artifactId>common-web</artifactId>
</dependency>
<dependency>
    <groupId>com.enterprise.platform</groupId>
    <artifactId>common-exception</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 4. Add to root POM modules

```xml
<module>services/notification-service</module>
```

### 5. Add Gateway route

```yaml
spring.cloud.gateway.routes:
  - id: notification-service
    uri: lb://NOTIFICATION-SERVICE
    predicates:
      - Path=/api/v1/notifications/**
```

### 6. Add to API Console

Add quick API buttons in `api-console/index.html` and the service to `SERVICE_MAP` in `app.js`.

## Failure Scenarios

### Demonstrate Service Down

```bash
# Stop user-service
# (Ctrl+C in user-service terminal)
```

Then call:
```
GET /api/v1/users/101
```

The console will show:
```
✓ CLIENT          COMPLETED
✓ API GATEWAY     COMPLETED
✓ DISCOVERY       COMPLETED
✕ USER SERVICE    FAILED - Connection unavailable
✕ RESPONSE        FAILED
```

### Demonstrate 404

```
GET /api/v1/users/999
```

Returns:
```json
{
  "success": false,
  "message": "User not found with id: 999",
  "errorCode": "USER_NOT_FOUND"
}
```

### Demonstrate Invalid Endpoint

```
GET /api/v1/invalid
```

Returns 404 from Gateway.

## How to Add a Dependency to the BOM

Edit `bom/pom.xml` and add to `<dependencyManagement>`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

Services then use without version:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-library</artifactId>
</dependency>
```

## Testing

### Run All Tests

```bash
mvn test
```

### Run Tests for Specific Module

```bash
cd services/user-service
mvn test
```

### Test Coverage

Tests cover:
- Controller layer (MockMvc)
- Service layer (Mockito)
- Exception handling (GlobalExceptionHandler)
- API response models

## Configuration

All services use externalized configuration via `application.yml`:

- Service ports are configured per service
- Eureka URL is configurable via environment variable
- Actuator endpoints are exposed for health checks

### Key Configuration Properties

| Property | Description |
|----------|-------------|
| `spring.application.name` | Service name for Eureka |
| `server.port` | HTTP port |
| `eureka.client.serviceUrl.defaultZone` | Eureka server URL |
| `management.endpoints.web.exposure.include` | Actuator endpoints |

## License

This is a reusable project skeleton for educational and development purposes.
