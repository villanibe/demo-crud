package com.democrud.presentation.article.dto;

import com.democrud.domain.Article;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating an article")
public record ArticleRequestDTO(
        @Schema(description = "Title of the article", example = "Understanding Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Title is required and cannot be blank")
        @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
        String title,
        
        @Schema(description = "Description or content of the article", example = "A comprehensive guide to Spring Boot development", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Description is required and cannot be blank")
        @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
        String description
) {

    public static Article toEntity(ArticleRequestDTO articleRequestDTO) {
        Article article = new Article();
        article.setTitle(articleRequestDTO.title);
        article.setDescription(articleRequestDTO.description);
        return article;
    }

}
