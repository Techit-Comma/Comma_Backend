package com.bitharmony.comma.member.dto;

import com.bitharmony.comma.member.entity.Member;

public record GithubMemberResponse(
        String id,
        String name,
        String email,
        String avatar_url
) {
    private static final String PROVIDER = "GITHUB";
    private static final String BASIC_NAME = "comma_user_";

    public Member toEntity() {
        return Member.builder()
                .username(BASIC_NAME + id)
                .email(email)
                .nickname(name + "_" + id)
                .imageUrl(avatar_url)
                .provider(PROVIDER)
                .providerId(id)
                .build();
    }

}
