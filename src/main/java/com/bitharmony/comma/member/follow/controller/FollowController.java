package com.bitharmony.comma.member.follow.controller;

import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.follow.dto.FollowingListResponse;
import com.bitharmony.comma.member.follow.service.FollowService;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.service.MemberService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;
    private final MemberService memberService;

    @PostMapping("/{username}")
    public GlobalResponse follow(@PathVariable("username") String followingUsername, Principal principal) {
        Member artist = memberService.getMemberByUsername(followingUsername);
        Member follower = memberService.getMemberByUsername(principal.getName());
        followService.follow(artist, follower);

        return GlobalResponse.of("201");
    }

    @DeleteMapping("/{username}")
    public GlobalResponse unfollow(@PathVariable("username") String followingUsername, Principal principal) {
        Member artist = memberService.getMemberByUsername(followingUsername);
        Member follower = memberService.getMemberByUsername(principal.getName());
        followService.unfollow(artist, follower);

        return GlobalResponse.of("200");
    }

    @GetMapping
    public GlobalResponse getAllFollowing(Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        List<FollowingListResponse> response = followService.getAllFollowingList(member);
        return GlobalResponse.of("200", response);
    }
}
