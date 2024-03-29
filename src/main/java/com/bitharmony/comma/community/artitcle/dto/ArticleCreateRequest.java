package com.bitharmony.comma.community.artitcle.dto;

import com.bitharmony.comma.community.artitcle.entity.Article;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ArticleCreateRequest(
        @NotNull(message = "카테고리를 선택해주세요.")
        Article.Category category,
        @NotBlank(message = "내용을 입력해주세요.")
        String content,
        @NotNull
        String artistUsername
) {
}
