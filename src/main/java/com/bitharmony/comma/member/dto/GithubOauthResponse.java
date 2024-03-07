package com.bitharmony.comma.member.dto;

public record GithubOauthResponse(
        String access_token,
        String scope,
        String token_type
) {

}
