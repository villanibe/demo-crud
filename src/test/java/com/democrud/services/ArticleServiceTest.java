package com.democrud.services;

import com.democrud.domain.Article;
import com.democrud.presentation.article.dto.ArticleResponseDTO;
import com.democrud.repositories.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    private Article testArticle;
    private Article savedArticle;

    @BeforeEach
    void setUp() {
        testArticle = new Article();
        testArticle.setTitle("Test Title");
        testArticle.setDescription("Test Description");
        testArticle.setPublished(false);

        savedArticle = new Article();
        savedArticle.setId(1L);
        savedArticle.setPublicId("test-uuid-123");
        savedArticle.setTitle("Test Title");
        savedArticle.setDescription("Test Description");
        savedArticle.setPublished(false);
    }

    @Test
    void createArticle_ShouldCreateArticleSuccessfully() {
        // Given
        when(articleRepository.save(any(Article.class))).thenReturn(savedArticle);

        // When
        ArticleResponseDTO result = articleService.createArticle(testArticle);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("test-uuid-123");
        assertThat(result.title()).isEqualTo("Test Title");
        assertThat(result.description()).isEqualTo("Test Description");
        assertThat(result.isPublished()).isFalse();

        verify(articleRepository).save(any(Article.class));
        assertThat(testArticle.getPublicId()).isNotNull(); // UUID should be set
    }

    @Test
    void findAllArticles_ShouldReturnAllArticles() {
        // Given
        Article article1 = new Article();
        article1.setPublicId("uuid-1");
        article1.setTitle("Title 1");
        article1.setDescription("Description 1");
        article1.setPublished(true);

        Article article2 = new Article();
        article2.setPublicId("uuid-2");
        article2.setTitle("Title 2");
        article2.setDescription("Description 2");
        article2.setPublished(false);

        List<Article> articles = Arrays.asList(article1, article2);
        when(articleRepository.findAll()).thenReturn(articles);

        // When
        List<ArticleResponseDTO> result = articleService.findAllArticles();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("uuid-1");
        assertThat(result.get(0).title()).isEqualTo("Title 1");
        assertThat(result.get(0).isPublished()).isTrue();
        assertThat(result.get(1).id()).isEqualTo("uuid-2");
        assertThat(result.get(1).title()).isEqualTo("Title 2");
        assertThat(result.get(1).isPublished()).isFalse();

        verify(articleRepository).findAll();
    }

    @Test
    void findAllArticles_ShouldReturnEmptyListWhenNoArticles() {
        // Given
        when(articleRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ArticleResponseDTO> result = articleService.findAllArticles();

        // Then
        assertThat(result).isEmpty();
        verify(articleRepository).findAll();
    }

    @Test
    void findArticleById_ShouldReturnArticleWhenExists() {
        // Given
        String publicId = "test-uuid-123";
        when(articleRepository.findByPublicId(publicId)).thenReturn(Optional.of(savedArticle));

        // When
        ArticleResponseDTO result = articleService.findArticleById(publicId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("test-uuid-123");
        assertThat(result.title()).isEqualTo("Test Title");
        assertThat(result.description()).isEqualTo("Test Description");
        assertThat(result.isPublished()).isFalse();

        verify(articleRepository).findByPublicId(publicId);
    }

    @Test
    void findArticleById_ShouldThrowExceptionWhenNotFound() {
        // Given
        String publicId = "non-existent-uuid";
        when(articleRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> articleService.findArticleById(publicId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Article not found with id: non-existent-uuid")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(articleRepository).findByPublicId(publicId);
    }

    @Test
    void deleteArticle_ShouldDeleteArticleWhenExists() {
        // Given
        String publicId = "test-uuid-123";
        when(articleRepository.findByPublicId(publicId)).thenReturn(Optional.of(savedArticle));

        // When
        articleService.deleteArticle(publicId);

        // Then
        verify(articleRepository).findByPublicId(publicId);
        verify(articleRepository).delete(savedArticle);
    }

    @Test
    void deleteArticle_ShouldThrowExceptionWhenNotFound() {
        // Given
        String publicId = "non-existent-uuid";
        when(articleRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> articleService.deleteArticle(publicId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Article not found with id: non-existent-uuid")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(articleRepository).findByPublicId(publicId);
        verify(articleRepository, never()).delete(any(Article.class));
    }
}
