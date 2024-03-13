package com.bitharmony.comma.member.follow.dto;

import com.bitharmony.comma.member.member.entity.Member;
import lombok.Builder;

@Builder
public record FollowingListResponse(
        Long memberId,
        String username
) {

    public static FollowingListResponse fromEntity(Member member) {
        return FollowingListResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .build();
    }
}
