package com.democrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for Demo CRUD API.
 * 
 * This application provides a RESTful API for managing articles with:
 * - Complete CRUD operations (Create, Read, Delete)
 * - PostgreSQL database integration
 * - Swagger/OpenAPI documentation
 * - Comprehensive testing
 * 
 * API Endpoints:
 * - POST   /api/articles     - Create new article
 * - GET    /api/articles     - Get all articles  
 * - GET    /api/articles/{id} - Get article by ID
 * - DELETE /api/articles/{id} - Delete article by ID
 * 
 * Documentation available at: http://localhost:8080/swagger-ui.html
 * 
 * @author Demo CRUD Team
 * @version 1.0.0
 */
@SpringBootApplication
public class DemoCrudApplication {

	/**
	 * Main method to start the Spring Boot application.
	 * 
	 * Application will start on port 8080 by default.
	 * Database connection configured for PostgreSQL on localhost:5432
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoCrudApplication.class, args);
	}

}
