package com.bitharmony.comma.member.member.dto;

import lombok.Builder;

@Builder
public record MemberImageResponse(
        String uploadFileName,
        String profileImageUrl
) {
}
