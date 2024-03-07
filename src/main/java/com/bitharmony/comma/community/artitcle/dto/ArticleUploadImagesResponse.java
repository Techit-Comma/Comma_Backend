package com.bitharmony.comma.community.artitcle.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ArticleUploadImagesResponse(
        List<String> imageUrls
) {
}
