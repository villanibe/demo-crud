# API Sequence Diagrams

This document contains sequence diagrams that illustrate the flow of operations in the Demo CRUD API.

## Create Article Flow

```mermaid
sequenceDiagram
    participant Client
    participant ArticleController
    participant ArticleService
    participant ArticleRepository
    participant PostgreSQL
    
    Client->>ArticleController: POST /api/articles
    Note over Client,ArticleController: Request Body: {title, description}
    
    ArticleController->>ArticleController: Validate @RequestBody
    ArticleController->>ArticleRequestDTO: toEntity(requestDTO)
    ArticleRequestDTO->>ArticleRequestDTO: Validate title & description
    
    alt Valid Request
        ArticleRequestDTO-->>ArticleController: Article entity
        ArticleController->>ArticleService: createArticle(entity)
        ArticleService->>ArticleService: Generate UUID for publicId
        ArticleService->>ArticleRepository: save(article)
        ArticleRepository->>PostgreSQL: INSERT INTO tb_articles
        PostgreSQL-->>ArticleRepository: Saved article with ID
        ArticleRepository-->>ArticleService: Article entity
        ArticleService->>ArticleService: Convert to ArticleResponseDTO
        ArticleService-->>ArticleController: ArticleResponseDTO
        ArticleController-->>Client: 201 CREATED + ArticleResponseDTO
    else Invalid Request
        ArticleRequestDTO->>ArticleRequestDTO: Throw ResponseStatusException
        ArticleRequestDTO-->>Client: 400 BAD REQUEST
    end
```

## Get All Articles Flow

```mermaid
sequenceDiagram
    participant Client
    participant ArticleController
    participant ArticleService
    participant ArticleRepository
    participant PostgreSQL
    
    Client->>ArticleController: GET /api/articles
    ArticleController->>ArticleService: findAllArticles()
    ArticleService->>ArticleRepository: findAll()
    ArticleRepository->>PostgreSQL: SELECT * FROM tb_articles
    PostgreSQL-->>ArticleRepository: List<Article>
    ArticleRepository-->>ArticleService: List<Article>
    ArticleService->>ArticleService: Convert to List<ArticleResponseDTO>
    ArticleService-->>ArticleController: List<ArticleResponseDTO>
    ArticleController-->>Client: 200 OK + List<ArticleResponseDTO>
```

## Get Article by ID Flow

```mermaid
sequenceDiagram
    participant Client
    participant ArticleController
    participant ArticleService
    participant ArticleRepository
    participant PostgreSQL
    
    Client->>ArticleController: GET /api/articles/{id}
    ArticleController->>ArticleService: findArticleById(id)
    ArticleService->>ArticleRepository: findByPublicId(id)
    ArticleRepository->>PostgreSQL: SELECT * FROM tb_articles WHERE public_id = ?
    
    alt Article Found
        PostgreSQL-->>ArticleRepository: Article entity
        ArticleRepository-->>ArticleService: Optional<Article> (present)
        ArticleService->>ArticleService: Convert to ArticleResponseDTO
        ArticleService-->>ArticleController: ArticleResponseDTO
        ArticleController-->>Client: 200 OK + ArticleResponseDTO
    else Article Not Found
        PostgreSQL-->>ArticleRepository: No results
        ArticleRepository-->>ArticleService: Optional<Article> (empty)
        ArticleService->>ArticleService: Throw ResponseStatusException
        ArticleService-->>Client: 404 NOT FOUND
    end
```

## Delete Article Flow

```mermaid
sequenceDiagram
    participant Client
    participant ArticleController
    participant ArticleService
    participant ArticleRepository
    participant PostgreSQL
    
    Client->>ArticleController: DELETE /api/articles/{id}
    ArticleController->>ArticleService: deleteArticle(id)
    ArticleService->>ArticleRepository: findByPublicId(id)
    ArticleRepository->>PostgreSQL: SELECT * FROM tb_articles WHERE public_id = ?
    
    alt Article Found
        PostgreSQL-->>ArticleRepository: Article entity
        ArticleRepository-->>ArticleService: Optional<Article> (present)
        ArticleService->>ArticleRepository: delete(article)
        ArticleRepository->>PostgreSQL: DELETE FROM tb_articles WHERE id = ?
        PostgreSQL-->>ArticleRepository: Deletion confirmed
        ArticleRepository-->>ArticleService: Void
        ArticleService-->>ArticleController: Void
        ArticleController-->>Client: 204 NO CONTENT
    else Article Not Found
        PostgreSQL-->>ArticleRepository: No results
        ArticleRepository-->>ArticleService: Optional<Article> (empty)
        ArticleService->>ArticleService: Throw ResponseStatusException
        ArticleService-->>Client: 404 NOT FOUND
    end
```

## Complete System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        C[Client/Browser]
        S[Swagger UI]
    end
    
    subgraph "Presentation Layer"
        AC[ArticleController]
        REQ[ArticleRequestDTO]
        RES[ArticleResponseDTO]
    end
    
    subgraph "Service Layer"
        AS[ArticleService]
    end
    
    subgraph "Repository Layer"
        AR[ArticleRepository]
    end
    
    subgraph "Domain Layer"
        A[Article Entity]
    end
    
    subgraph "Database Layer"
        DB[(PostgreSQL)]
    end
    
    C --> AC
    S --> AC
    AC --> REQ
    AC --> RES
    AC --> AS
    AS --> AR
    AR --> A
    AR --> DB
    
    style C fill:#e1f5fe
    style S fill:#e8f5e8
    style AC fill:#fff3e0
    style AS fill:#f3e5f5
    style AR fill:#fce4ec
    style DB fill:#e0f2f1
```

## Error Handling Flow

```mermaid
sequenceDiagram
    participant Client
    participant ArticleController
    participant ArticleService
    participant Spring Framework
    
    Client->>ArticleController: API Request
    
    alt Business Logic Error
        ArticleController->>ArticleService: Service call
        ArticleService->>ArticleService: Throw ResponseStatusException
        ArticleService-->>ArticleController: ResponseStatusException
        ArticleController-->>Spring Framework: Exception bubbles up
        Spring Framework-->>Client: HTTP Error Response (400/404)
    else Validation Error
        ArticleController->>ArticleController: Validation fails
        ArticleController-->>Spring Framework: Validation exception
        Spring Framework-->>Client: 400 BAD REQUEST
    else Unexpected Error
        ArticleController->>ArticleService: Service call
        ArticleService->>ArticleService: Runtime exception
        ArticleService-->>Spring Framework: Exception bubbles up
        Spring Framework-->>Client: 500 INTERNAL SERVER ERROR
    end
```

## Transaction Flow

```mermaid
sequenceDiagram
    participant Client
    participant ArticleController
    participant ArticleService
    participant TransactionManager
    participant ArticleRepository
    participant PostgreSQL
    
    Client->>ArticleController: POST/DELETE Request
    ArticleController->>ArticleService: Service method call
    
    Note over ArticleService: @Transactional annotation
    ArticleService->>TransactionManager: Begin transaction
    TransactionManager->>PostgreSQL: BEGIN
    
    ArticleService->>ArticleRepository: Repository operation
    ArticleRepository->>PostgreSQL: SQL operation
    
    alt Success
        PostgreSQL-->>ArticleRepository: Operation successful
        ArticleRepository-->>ArticleService: Success
        ArticleService->>TransactionManager: Commit transaction
        TransactionManager->>PostgreSQL: COMMIT
        PostgreSQL-->>TransactionManager: Transaction committed
        TransactionManager-->>ArticleService: Success
        ArticleService-->>ArticleController: Success response
        ArticleController-->>Client: HTTP Success
    else Error
        PostgreSQL-->>ArticleRepository: Operation failed
        ArticleRepository-->>ArticleService: Exception
        ArticleService->>TransactionManager: Rollback transaction
        TransactionManager->>PostgreSQL: ROLLBACK
        PostgreSQL-->>TransactionManager: Transaction rolled back
        TransactionManager-->>ArticleService: Rollback complete
        ArticleService-->>ArticleController: Exception
        ArticleController-->>Client: HTTP Error
    end
```
