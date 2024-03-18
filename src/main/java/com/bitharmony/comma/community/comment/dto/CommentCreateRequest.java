package com.bitharmony.comma.community.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CommentCreateRequest(
        @NotNull
        long articleId,
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {

}
