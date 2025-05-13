package com.democrud.presentation.article;

import com.democrud.presentation.article.dto.ArticleRequestDTO;
import com.democrud.presentation.article.dto.ArticleResponseDTO;
import com.democrud.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/articles")
@RestController
public class ArticleController {

    private final ArticleService articleService;


    @PostMapping
    public ResponseEntity<ArticleResponseDTO> createArticle(@RequestBody ArticleRequestDTO createArticleRequestDTO) {

        ArticleResponseDTO articleResponseDTO = articleService.createArticle(
                ArticleRequestDTO.toEntity(createArticleRequestDTO)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> findAllArticles() {
        return ResponseEntity.ok(articleService.findAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> findArticleById(@PathVariable String id) {
        return ResponseEntity.ok(articleService.findArticleById(id));
    }

}
