package com.democrud.presentation.article;

import com.democrud.domain.Article;
import com.democrud.presentation.article.dto.ArticleRequestDTO;
import com.democrud.presentation.article.dto.ArticleResponseDTO;
import com.democrud.services.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @Autowired
    private ObjectMapper objectMapper;

    private ArticleRequestDTO articleRequestDTO;
    private ArticleResponseDTO articleResponseDTO;

    @BeforeEach
    void setUp() {
        articleRequestDTO = new ArticleRequestDTO("Test Title", "Test Description");
        articleResponseDTO = new ArticleResponseDTO("test-uuid-123", "Test Title", "Test Description", false);
    }

    @Test
    void createArticle_ShouldCreateArticleSuccessfully() throws Exception {
        // Given
        when(articleService.createArticle(any(Article.class))).thenReturn(articleResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-uuid-123"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.isPublished").value(false));

        verify(articleService).createArticle(any(Article.class));
    }

    @Test
    void createArticle_ShouldReturnBadRequestForInvalidData() throws Exception {
        // Given
        ArticleRequestDTO invalidRequest = new ArticleRequestDTO("", ""); // Invalid data

        // When & Then
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllArticles_ShouldReturnAllArticles() throws Exception {
        // Given
        List<ArticleResponseDTO> articles = Arrays.asList(
                new ArticleResponseDTO("uuid-1", "Title 1", "Description 1", true),
                new ArticleResponseDTO("uuid-2", "Title 2", "Description 2", false)
        );
        when(articleService.findAllArticles()).thenReturn(articles);

        // When & Then
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("uuid-1"))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].isPublished").value(true))
                .andExpect(jsonPath("$[1].id").value("uuid-2"))
                .andExpect(jsonPath("$[1].title").value("Title 2"))
                .andExpect(jsonPath("$[1].isPublished").value(false));

        verify(articleService).findAllArticles();
    }

    @Test
    void findAllArticles_ShouldReturnEmptyArrayWhenNoArticles() throws Exception {
        // Given
        when(articleService.findAllArticles()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(articleService).findAllArticles();
    }

    @Test
    void findArticleById_ShouldReturnArticleWhenExists() throws Exception {
        // Given
        String articleId = "test-uuid-123";
        when(articleService.findArticleById(articleId)).thenReturn(articleResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/articles/{id}", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-uuid-123"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.isPublished").value(false));

        verify(articleService).findArticleById(articleId);
    }

    @Test
    void findArticleById_ShouldReturnNotFoundWhenArticleDoesNotExist() throws Exception {
        // Given
        String articleId = "non-existent-uuid";
        when(articleService.findArticleById(articleId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        // When & Then
        mockMvc.perform(get("/api/articles/{id}", articleId))
                .andExpect(status().isNotFound());

        verify(articleService).findArticleById(articleId);
    }

    @Test
    void deleteArticle_ShouldDeleteArticleSuccessfully() throws Exception {
        // Given
        String articleId = "test-uuid-123";
        doNothing().when(articleService).deleteArticle(articleId);

        // When & Then
        mockMvc.perform(delete("/api/articles/{id}", articleId))
                .andExpect(status().isNoContent());

        verify(articleService).deleteArticle(articleId);
    }

    @Test
    void deleteArticle_ShouldReturnNotFoundWhenArticleDoesNotExist() throws Exception {
        // Given
        String articleId = "non-existent-uuid";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"))
                .when(articleService).deleteArticle(articleId);

        // When & Then
        mockMvc.perform(delete("/api/articles/{id}", articleId))
                .andExpect(status().isNotFound());

        verify(articleService).deleteArticle(articleId);
    }
}
