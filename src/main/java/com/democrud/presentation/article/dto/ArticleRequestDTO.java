package com.democrud.presentation.article.dto;

import com.democrud.domain.Article;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

public record ArticleRequestDTO(String title, String description) {

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
