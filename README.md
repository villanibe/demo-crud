# Demo CRUD - Spring Boot Article Management API

A demonstration Spring Boot REST API application that implements complete CRUD (Create, Read, Update, Delete) operations for managing articles. This project showcases modern Java development practices using Spring Boot 3.x, PostgreSQL, and comprehensive testing.

## 🚀 Features

- **Complete CRUD Operations**: Create, Read, Update, and Delete articles
- **RESTful API**: Clean REST endpoints following HTTP standards
- **PostgreSQL Integration**: Persistent data storage with JPA/Hibernate
- **UUID-based Public IDs**: Secure external identifiers separate from database IDs
- **Comprehensive Testing**: Unit and integration tests with high coverage
- **Swagger/OpenAPI Documentation**: Interactive API documentation and testing interface
- **Docker Support**: Easy database setup with Docker Compose
- **Modern Java**: Built with Java 21 and Spring Boot 3.4.5

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.4.5
- **Language**: Java 21
- **Database**: PostgreSQL 13
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Documentation**: Swagger/OpenAPI 3
- **Libraries**: 
  - Lombok (reducing boilerplate code)
  - Spring Web (REST API)
  - Spring Data JPA (database operations)
  - AssertJ (fluent assertions)
  - SpringDoc OpenAPI (API documentation)

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (for database)
- Git

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/democrud/
│   │   ├── DemoCrudApplication.java           # Main application class
│   │   ├── domain/
│   │   │   └── Article.java                  # Article entity
│   │   ├── presentation/
│   │   │   └── article/
│   │   │       ├── ArticleController.java    # REST controller
│   │   │       └── dto/
│   │   │           ├── ArticleRequestDTO.java
│   │   │           └── ArticleResponseDTO.java
│   │   ├── repositories/
│   │   │   └── ArticleRepository.java        # JPA repository
│   │   └── services/
│   │       └── ArticleService.java           # Business logic
│   └── resources/
│       ├── application.properties            # Configuration
│       ├── static/
│       └── templates/
└── test/
    └── java/com/democrud/
        ├── DemoCrudApplicationTests.java
        ├── services/
        │   └── ArticleServiceTest.java       # Service unit tests
        └── presentation/article/
            └── ArticleControllerTest.java    # Controller integration tests
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/villanibe/demo-crud.git
cd demo-crud
```

### 2. Start PostgreSQL Database

```bash
docker-compose up -d
```

This will start a PostgreSQL container with:
- **Database**: `demo_crud`
- **Username**: `demo`
- **Password**: `demo123`
- **Port**: `5432`

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

Or on Windows:
```bash
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access Swagger Documentation

Once the application is running, you can access the interactive API documentation at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

The Swagger UI provides an interactive interface where you can:
- View all available endpoints
- Test API calls directly from the browser
- See request/response schemas
- Understand error responses

### 4. Run Tests

```bash
./mvnw test
```

## � API Testing with Swagger

The project includes **Swagger/OpenAPI 3** documentation for easy API testing and exploration.

### Accessing Swagger UI

1. Start the application: `./mvnw spring-boot:run`
2. Open your browser and navigate to: **http://localhost:8080/swagger-ui.html**

### Swagger Features

- **Interactive Testing**: Test all endpoints directly from the browser
- **Request/Response Examples**: See sample data for all operations  
- **Schema Documentation**: Detailed information about request and response models
- **Error Response Documentation**: Understanding of all possible error scenarios
- **Try It Out**: Execute real API calls with custom parameters

### API Endpoints in Swagger

All CRUD operations are documented and testable:

| Operation | Endpoint | Description |
|-----------|----------|-------------|
| 🟢 POST | `/api/articles` | Create new article |
| 🔵 GET | `/api/articles` | Get all articles |
| 🔵 GET | `/api/articles/{id}` | Get article by ID |
| 🔴 DELETE | `/api/articles/{id}` | Delete article |

## �📚 API Documentation

### Base URL
```
http://localhost:8080/api/articles
```

### Endpoints

#### Create Article
```http
POST /api/articles
Content-Type: application/json

{
    "title": "Article Title",
    "description": "Article description content"
}
```

**Response (201 Created):**
```json
{
    "id": "uuid-string",
    "title": "Article Title",
    "description": "Article description content",
    "isPublished": false
}
```

#### Get All Articles
```http
GET /api/articles
```

**Response (200 OK):**
```json
[
    {
        "id": "uuid-string-1",
        "title": "First Article",
        "description": "Description of first article",
        "isPublished": true
    },
    {
        "id": "uuid-string-2",
        "title": "Second Article",
        "description": "Description of second article",
        "isPublished": false
    }
]
```

#### Get Article by ID
```http
GET /api/articles/{id}
```

**Response (200 OK):**
```json
{
    "id": "uuid-string",
    "title": "Article Title",
    "description": "Article description content",
    "isPublished": false
}
```

**Response (404 Not Found):**
```json
{
    "timestamp": "2025-08-28T10:30:00.000+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Article not found with id: non-existent-uuid",
    "path": "/api/articles/non-existent-uuid"
}
```

#### Delete Article
```http
DELETE /api/articles/{id}
```

**Response (204 No Content):** Empty body

**Response (404 Not Found):** Same as GET by ID

### Example Usage with cURL

```bash
# Create an article
curl -X POST http://localhost:8080/api/articles \
  -H "Content-Type: application/json" \
  -d '{"title": "My First Article", "description": "This is a test article"}'

# Get all articles
curl http://localhost:8080/api/articles

# Get specific article (replace {id} with actual UUID)
curl http://localhost:8080/api/articles/{id}

# Delete an article (replace {id} with actual UUID)
curl -X DELETE http://localhost:8080/api/articles/{id}
```

## 🧪 Testing

The project includes comprehensive test coverage:

### Unit Tests
- **ArticleServiceTest**: Tests business logic with mocked dependencies
- **ArticleControllerTest**: Tests REST endpoints with MockMvc

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ArticleServiceTest

# Run tests with coverage
./mvnw test jacoco:report
```

### Test Categories
- ✅ Service layer logic
- ✅ Controller REST endpoints
- ✅ Error handling scenarios
- ✅ Validation testing
- ✅ Database operations (mocked)

## 📊 Database Schema

### Articles Table (`tb_articles`)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Internal database ID |
| public_id | VARCHAR(255) | UNIQUE, INDEXED | External UUID identifier |
| title | VARCHAR(255) | NOT NULL | Article title |
| description | TEXT | NOT NULL | Article content description |
| is_published | BOOLEAN | DEFAULT FALSE | Publication status |

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/demo_crud
spring.datasource.username=demo
spring.datasource.password=demo123

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Environment Variables
You can override default configurations using environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-host:5432/your-db
export SPRING_DATASOURCE_USERNAME=your-username
export SPRING_DATASOURCE_PASSWORD=your-password
```

## 🚀 Deployment

### Building for Production
```bash
./mvnw clean package -DskipTests
```

The JAR file will be created in `target/demo-crud-0.0.1-SNAPSHOT.jar`

### Running the JAR
```bash
java -jar target/demo-crud-0.0.1-SNAPSHOT.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **villanibe** - *Initial work* - [GitHub](https://github.com/villanibe)

## 🐛 Issues

Found a bug or have a suggestion? Please create an issue on [GitHub Issues](https://github.com/villanibe/demo-crud/issues).

## 📈 Roadmap

- [ ] Add UPDATE endpoint for articles
- [ ] Implement pagination for article listing
- [ ] Add article search functionality
- [ ] Implement user authentication
- [ ] Add article categories/tags
- [x] API documentation with Swagger/OpenAPI
- [ ] Add caching with Redis
- [ ] Implement soft delete functionality

---

**Happy Coding!** 🎉
