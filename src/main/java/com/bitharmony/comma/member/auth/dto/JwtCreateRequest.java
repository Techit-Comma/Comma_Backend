package com.bitharmony.comma.member.auth.dto;

import lombok.Builder;

@Builder
public record JwtCreateRequest (
        long id,
        String username
){
}
