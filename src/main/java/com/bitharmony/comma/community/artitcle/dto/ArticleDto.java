package com.bitharmony.comma.community.artitcle.dto;

import com.bitharmony.comma.community.artitcle.entity.Article;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ArticleDto(
        long id,
        String username,
        String userProfile,
        Article.Category category,
        String content,
        LocalDateTime createDate,
        LocalDateTime modifyDate
) {
    public ArticleDto(Article article){
        this(
                article.getId(),
                article.getWriter().getUsername(),
                article.getWriter().getImageUrl(),
                article.getCategory(),
                article.getContent(),
                article.getCreateDate(),
                article.getModifyDate()
        );
    }

}
