package com.democrud.presentation.article.dto;

import com.democrud.domain.Article;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Schema(description = "Request DTO for creating an article")
public record ArticleRequestDTO(
        @Schema(description = "Title of the article", example = "Understanding Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        
        @Schema(description = "Description or content of the article", example = "A comprehensive guide to Spring Boot development", requiredMode = Schema.RequiredMode.REQUIRED)
        String description
) {

    public static Article toEntity(ArticleRequestDTO articleRequestDTO) {
        if( isValidRequest(articleRequestDTO)) {
            Article article = new Article();
            article.setTitle(articleRequestDTO.title);
            article.setDescription(articleRequestDTO.description);
            return article;
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid params");
    }

    private static boolean isValidRequest(ArticleRequestDTO articleRequestDTO) {
        return StringUtils.hasLength(articleRequestDTO.title) &&
                StringUtils.hasLength(articleRequestDTO.description);
    }

}
