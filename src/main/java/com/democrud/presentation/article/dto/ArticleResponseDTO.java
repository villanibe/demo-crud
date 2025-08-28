package com.democrud.presentation.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for article data")
public record ArticleResponseDTO(
        @Schema(description = "Unique identifier of the article", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,
        
        @Schema(description = "Title of the article", example = "Understanding Spring Boot")
        String title,
        
        @Schema(description = "Description or content of the article", example = "A comprehensive guide to Spring Boot development")
        String description,
        
        @Schema(description = "Publication status of the article", example = "false")
        boolean isPublished
) { }
