package com.bitharmony.comma.member.dto;

import lombok.Builder;

@Builder
public record MemberImageResponse(
        String uploadFileName,
        String profileImageUrl
) {
}
