package com.bitharmony.comma.member.member.dto;

import lombok.Builder;

@Builder
public record MemberLoginResponse (
        Long memberId,
        String username,
        String accessToken,
        String refreshToken
){
}
