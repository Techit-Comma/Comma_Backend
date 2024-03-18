package com.bitharmony.comma.member.auth.dto;

public record GoogleOauthResponse(
        String access_token,
        String expires_in,
        String scope,
        String token_type,
        String refresh_token,
        String id_token
) {

}
