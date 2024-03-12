package com.bitharmony.comma.member.auth.dto;

import lombok.Builder;

@Builder
public record JwtRegenerateRequest(
        String refreshToken
) {
}
