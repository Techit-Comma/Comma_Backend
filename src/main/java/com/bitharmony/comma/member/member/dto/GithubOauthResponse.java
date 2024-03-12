package com.bitharmony.comma.member.member.dto;

public record GithubOauthResponse(
        String access_token,
        String scope,
        String token_type
) {

}
