package com.bitharmony.comma.member.follow.service;

import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.global.exception.member.DuplicateFollowException;
import com.bitharmony.comma.global.exception.member.FollowNotFoundException;
import com.bitharmony.comma.global.exception.member.SelfFollowException;
import com.bitharmony.comma.member.follow.dto.FollowingListResponse;
import com.bitharmony.comma.member.follow.entity.Follow;
import com.bitharmony.comma.member.follow.repository.FollowRepository;
import com.bitharmony.comma.member.member.service.MemberService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional
    public void follow(Member artist, Member follower) {
        if (artist.equals(follower)) {
            throw new SelfFollowException();
        }

        Optional<Follow> isExist = followRepository.findByFollowerIdAndFollowingId(follower.getId(),
                artist.getId());

        if (isExist.isPresent()) {
            throw new DuplicateFollowException();
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(artist)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Member artist, Member follower) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(follower.getId(), artist.getId())
                .orElseThrow(FollowNotFoundException::new);

        followRepository.delete(follow);
    }

    public List<FollowingListResponse> getAllFollowingList(Member member) {
        return member.getFollowingList().stream()
                .map((follow) -> FollowingListResponse.fromEntity(follow.getFollowing())).toList();
    }

    public List<Member> getAllFollowerList(Member artist) {
        return artist.getFollowerList().stream()
                .map(Follow::getFollower).toList();
    }
}
