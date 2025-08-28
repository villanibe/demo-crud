package com.democrud.services;

import com.democrud.domain.Article;
import com.democrud.presentation.article.dto.ArticleResponseDTO;
import com.democrud.repositories.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleResponseDTO createArticle(Article entity) {

        entity.setPublicId(UUID.randomUUID().toString());

        Article saved = articleRepository.save(entity);
        return new ArticleResponseDTO(
                saved.getPublicId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.isPublished()
        );
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDTO> findAllArticles() {
        return articleRepository.findAll()
                .stream()
                .map(model -> new ArticleResponseDTO(
                        model.getPublicId(),
                        model.getTitle(),
                        model.getDescription(),
                        model.isPublished())
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public ArticleResponseDTO findArticleById(final String articleId) {

        Optional<Article> optional = articleRepository.findByPublicId(articleId);
        if (optional.isPresent()) {

            return new ArticleResponseDTO(
                    optional.get().getPublicId(),
                    optional.get().getTitle(),
                    optional.get().getDescription(),
                    optional.get().isPublished());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Article not found with id: %s".formatted(articleId));
    }

    @Transactional
    public void deleteArticle(final String articleId) {
        Optional<Article> optional = articleRepository.findByPublicId(articleId);
        if (optional.isPresent()) {
            articleRepository.delete(optional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Article not found with id: %s".formatted(articleId));
        }
    }
}
