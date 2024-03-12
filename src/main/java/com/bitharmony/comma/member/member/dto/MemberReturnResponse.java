package com.bitharmony.comma.member.member.dto;

import lombok.Builder;

@Builder
public record MemberReturnResponse(
        String username,
        String Email,
        String nickname,
        String profileImageUrl
) {
}
