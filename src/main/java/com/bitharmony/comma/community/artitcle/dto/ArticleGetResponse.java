package com.bitharmony.comma.community.artitcle.dto;

import com.bitharmony.comma.community.artitcle.entity.Article;
import com.bitharmony.comma.community.artitcle.entity.ArticleImage;
import com.bitharmony.comma.member.entity.Member;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
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
