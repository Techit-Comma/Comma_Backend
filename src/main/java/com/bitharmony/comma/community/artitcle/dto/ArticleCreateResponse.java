package com.bitharmony.comma.community.artitcle.dto;

import lombok.Builder;

@Builder
public record ArticleCreateResponse(
        long articleId
) {
}
