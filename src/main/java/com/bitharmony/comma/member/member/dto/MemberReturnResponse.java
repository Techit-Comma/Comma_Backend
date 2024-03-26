package com.bitharmony.comma.member.member.dto;

import lombok.Builder;

@Builder
public record MemberReturnResponse(
        Long memberId,
        String username,
        String email,
        String nickname,
        String profileImageUrl
) {
}
