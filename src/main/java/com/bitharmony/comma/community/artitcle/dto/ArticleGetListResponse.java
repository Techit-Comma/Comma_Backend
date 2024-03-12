package com.bitharmony.comma.community.artitcle.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record ArticleGetListResponse(
        Page<ArticleDto> articleList
) {
}
