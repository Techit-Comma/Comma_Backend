package com.bitharmony.comma.community.artitcle.dto;

import com.bitharmony.comma.community.artitcle.entity.Article;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ArticleGetResponse(
        long id,
        String username,
        Article.Category category,
        String title,
        String content,
        Map<Long, String> imageUrls,
        LocalDateTime createDate,
        LocalDateTime modifyDate
) {
}
