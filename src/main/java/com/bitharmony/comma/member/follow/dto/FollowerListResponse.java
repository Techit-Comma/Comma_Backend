package com.bitharmony.comma.member.follow.dto;

import com.bitharmony.comma.member.member.entity.Member;
import lombok.Builder;

@Builder
public record FollowerListResponse(
        Long memberId,
        String username
) {

    public static FollowerListResponse fromEntity(Member member) {
        return FollowerListResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .build();
    }
}
