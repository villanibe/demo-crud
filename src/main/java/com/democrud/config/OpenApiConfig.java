package com.democrud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server")
                ))
                .info(new Info()
                        .title("Demo CRUD API")
                        .description("A comprehensive Spring Boot REST API for managing articles with full CRUD operations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Demo CRUD Team")
                                .email("contact@democrud.com")
                                .url("https://github.com/villanibe/demo-crud"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
