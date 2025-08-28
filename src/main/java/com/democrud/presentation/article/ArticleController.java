package com.democrud.presentation.article;

import com.democrud.presentation.article.dto.ArticleRequestDTO;
import com.democrud.presentation.article.dto.ArticleResponseDTO;
import com.democrud.services.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Articles", description = "Article management endpoints")
@RequiredArgsConstructor
@RequestMapping("/api/articles")
@RestController
public class ArticleController {

    private final ArticleService articleService;

    @Operation(
            summary = "Create a new article",
            description = "Creates a new article with the provided title and description. Returns the created article with a generated UUID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Article created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<ArticleResponseDTO> createArticle(@RequestBody ArticleRequestDTO createArticleRequestDTO) {

        ArticleResponseDTO articleResponseDTO = articleService.createArticle(
                ArticleRequestDTO.toEntity(createArticleRequestDTO)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleResponseDTO);
    }

    @Operation(
            summary = "Get all articles",
            description = "Retrieves a list of all articles in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> findAllArticles() {
        return ResponseEntity.ok(articleService.findAllArticles());
    }

    @Operation(
            summary = "Get article by ID",
            description = "Retrieves a specific article by its UUID identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArticleResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> findArticleById(
            @Parameter(description = "UUID of the article to retrieve", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(articleService.findArticleById(id));
    }

    @Operation(
            summary = "Delete article by ID",
            description = "Deletes a specific article by its UUID identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(
            @Parameter(description = "UUID of the article to delete", required = true)
            @PathVariable String id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

}
