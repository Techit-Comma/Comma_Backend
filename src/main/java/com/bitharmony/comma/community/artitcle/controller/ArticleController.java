package com.bitharmony.comma.community.artitcle.controller;

import com.bitharmony.comma.community.artitcle.dto.*;
import com.bitharmony.comma.community.artitcle.entity.Article;
import com.bitharmony.comma.community.artitcle.entity.ArticleImage;
import com.bitharmony.comma.community.artitcle.service.ArticleImageService;
import com.bitharmony.comma.community.artitcle.service.ArticleService;
import com.bitharmony.comma.global.exception.NotAuthorizedException;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/community/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final MemberService memberService;
    private final ArticleImageService articleImageService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<ArticleGetResponse> getArticle(@PathVariable long id) {

        Article article = articleService.getArticleById(id);
        Map<Long, String> imageUrls = articleImageService.getArticleImageByArticleId(id);

        return GlobalResponse.of(
                "200",
                ArticleGetResponse.builder()
                        .id(article.getId())
                        .username(article.getWriter().getUsername())
                        .category(article.getCategory())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .imageUrls(imageUrls)
                        .createDate(article.getCreateDate())
                        .modifyDate(article.getModifyDate())
                        .build()
        );
    }

    @GetMapping("")
    public GlobalResponse<ArticleGetListResponse> getArticleList(
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));

        Page<Article> articles = articleService.getArticleList(pageable);

        return GlobalResponse.of(
                "200",
                ArticleGetListResponse.builder()
                        .articleList(articles.map(ArticleDto::new))
                        .build()
        );
    }

    @GetMapping("/user/{artistUsername}")
    public GlobalResponse<ArticleGetListResponse> getArticleListByArtistIdAndCategory(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "category", defaultValue = "") Article.Category category,
            @PathVariable String artistUsername
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));

        Member member = memberService.getMemberByUsername(artistUsername);

        Page<Article> articles = articleService.getArticleListByArtistIdAndCategory(member.getId(), category, pageable);

        return GlobalResponse.of(
                "200",
                ArticleGetListResponse.builder()
                        .articleList(articles.map(ArticleDto::new))
                        .build()
        );
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<ArticleGetMyListResponse> getMyArticle(Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());

        List<Article> articles = articleService.getArticleByMemberId(member.getId());

        return GlobalResponse.of(
                "200",
                ArticleGetMyListResponse.builder()
                        .myList(articles.stream().map(ArticleDto::new).toList())
                        .build()
        );
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<ArticleCreateResponse> createArticle(
            @RequestBody @Valid ArticleCreateRequest request,
            Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        Member artist = memberService.getMemberByUsername(request.artistUsername());

        if (member != artist) {
            throw new NotAuthorizedException();
        }

        Article article = articleService.write(member, request.category(), request.title(), request.content(), artist);

        return GlobalResponse.of(
                "200",
                ArticleCreateResponse.builder()
                        .articleId(article.getId())
                        .build()
        );
    }

    @PostMapping("/images")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<ArticleUploadImagesResponse> uploadImages(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("articleId") Long articleId
    ) {
        List<String> imageUrls = new ArrayList<>();
        Article article = articleService.getArticleById(articleId);
        for(MultipartFile file: files) {
            String imageUrl = articleImageService.uploadFile(file);
            articleImageService.saveImageUrl(article, imageUrl);
            imageUrls.add(imageUrl);
        }

        return GlobalResponse.of(
                "200",
                ArticleUploadImagesResponse.builder()
                        .imageUrls(imageUrls)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<ArticleModifyResponse> modifyArticle(
            @PathVariable long id, Principal principal, @RequestBody @Valid ArticleModifyRequest request) {

        Member member = memberService.getMemberByUsername(principal.getName());
        Article article = articleService.getArticleById(id);

        if (!article.getWriter().equals(member)) {
            throw new NotAuthorizedException();
        }

        articleService.modifyArticle(article, request);

        return GlobalResponse.of(
                "200",
                ArticleModifyResponse.builder()
                        .id(article.getId())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<Void> deleteArticle(@PathVariable long id, Principal principal) {

        Member member = memberService.getMemberByUsername(principal.getName());
        Article article = articleService.getArticleById(id);

        if (!article.getWriter().equals(member)) {
            throw new NotAuthorizedException();
        }

        List<ArticleImage> articleImages = article.getImageUrl();
        for(ArticleImage articleImage : articleImages) {
            articleImageService.deleteFile(articleImage.getImageUrl());
        }

        articleService.deleteArticle(id);

        return GlobalResponse.of(
                "204"
        );
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<Void> deleteArticleImage(
            @PathVariable long imageId, Principal principal
    ) {
        ArticleImage articleImage = articleImageService.getArticleImageById(imageId);
            articleImageService.deleteFile(articleImage.getImageUrl());
            articleImageService.deleteArticleImage(imageId);

            return GlobalResponse.of("204");
    }

}
