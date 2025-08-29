package com.democrud.config.exception;

import com.democrud.presentation.article.ArticleController;
import com.democrud.services.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for GlobalExceptionHandler.
 * 
 * Verifies that error handling works correctly across different
 * error scenarios and produces the expected error response format.
 */
@WebMvcTest(ArticleController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @Test
    void shouldHandleValidationErrors() throws Exception {
        // Given: Invalid request with blank title and description
        String invalidJson = """
                {
                    "title": "",
                    "description": ""
                }
                """;

        // When & Then: Should return 400 with validation errors
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Validation failed for one or more fields"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.path").value("/api/articles"))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors.length()").value(4)); // Both @NotBlank and @Size for each field
    }

    @Test
    void shouldHandleResponseStatusException() throws Exception {
        // Given: Service throws ResponseStatusException
        when(articleService.findArticleById(anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        // When & Then: Should return 404 with proper error format
        mockMvc.perform(get("/api/articles/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("Article not found"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.path").value("/api/articles/non-existent-id"));
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        // Given: Malformed JSON request
        String malformedJson = """
                {
                    "title": "Valid Title",
                    "description": "Valid Description"
                    // Missing closing brace
                """;

        // When & Then: Should return 400 with malformed JSON error
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed JSON request"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists());
    }

    @Test
    void shouldHandleMethodNotAllowed() throws Exception {
        // When & Then: Should return 405 for unsupported HTTP method
        mockMvc.perform(patch("/api/articles"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("HTTP method not supported"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists());
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        // Given: Service throws unexpected exception
        when(articleService.findAllArticles())
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then: Should return 500 with generic error message
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists());
    }

    @Test
    void shouldHandleCustomArticleException() throws Exception {
        // Given: Service throws custom ArticleException
        when(articleService.findArticleById(anyString()))
                .thenThrow(ArticleException.notFound("test-id"));

        // When & Then: Should return proper error response
        mockMvc.perform(get("/api/articles/test-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("Article not found with id: test-id"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errorId").exists());
    }

    @Test
    void shouldValidateFieldSizes() throws Exception {
        // Given: Request with title too short and description too short (not too long for easier testing)
        String invalidJson = """
                {
                    "title": "Hi",
                    "description": "Short"
                }
                """;

        // When & Then: Should return validation errors for both fields
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors.length()").value(2)); // Only @Size violations
    }
}
