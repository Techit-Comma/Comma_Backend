package com.bitharmony.comma.member.dto;

import com.bitharmony.comma.member.entity.Member;

public record GoogleMemberInfo(
        String sub,
        String name,
        String email,
        String picture
) {
    private static final String PROVIDER = "GOOGLE";
    private static final String BASIC_NAME = "comma_user_";

    public Member toEntity() {
        return Member.builder()
                .username(BASIC_NAME + sub)
                .email(email)
                .nickname(name + "_" + sub)
                .imageUrl(picture)
                .provider(PROVIDER)
                .providerId(sub)
                .build();
    }

}
