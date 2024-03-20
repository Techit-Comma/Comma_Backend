package com.bitharmony.comma.member.member.controller;

import com.bitharmony.comma.global.exception.member.NotAuthorizedException;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.member.dto.MemberImageResponse;
import com.bitharmony.comma.member.member.dto.MemberJoinRequest;
import com.bitharmony.comma.member.member.dto.MemberLoginRequest;
import com.bitharmony.comma.member.member.dto.MemberModifyRequest;
import com.bitharmony.comma.member.member.dto.MemberPwModifyRequest;
import com.bitharmony.comma.member.member.dto.MemberReturnResponse;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.service.ProfileImageService;
import com.bitharmony.comma.member.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final ProfileImageService profileImageService;

    @PostMapping("/login")
    public GlobalResponse login(@RequestBody @Valid MemberLoginRequest memberLoginRequest) {
        return GlobalResponse.of("200",
                memberService.login(memberLoginRequest.username(), memberLoginRequest.password()));
    }

    @PostMapping("/join")
    public GlobalResponse join(@RequestBody @Valid MemberJoinRequest memberJoinRequest) {

        memberService.join(
                memberJoinRequest.username(), memberJoinRequest.password(), memberJoinRequest.passwordCheck(),
                memberJoinRequest.email(), memberJoinRequest.nickname());

        return GlobalResponse.of("201");
    }

    @PostMapping("/logout")
    public GlobalResponse logout() {
        memberService.logout();
        return GlobalResponse.of("200");
    }


    @GetMapping("/mypage")
    public GlobalResponse mypage() {
        MemberReturnResponse response = memberService.getProfile();
        return GlobalResponse.of("200", response);
    }

    @GetMapping("/check")
    public GlobalResponse check() {
        if (!memberService.getUser().isCredentialsNonExpired()) {
            throw new NotAuthorizedException();
        }
        return GlobalResponse.of("200");
    }

    @GetMapping("/{username}")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<MemberReturnResponse> profile(@PathVariable("username") String username) {
        MemberReturnResponse response = memberService.getProfile(username);
        return GlobalResponse.of("200", response);
    }

    @PutMapping("/modify")
    public GlobalResponse modify(@RequestBody @Valid MemberModifyRequest memberModifyRequest) {
        memberService.modify(memberModifyRequest.nickname(), memberModifyRequest.email());
        return GlobalResponse.of("200");
    }

    @PutMapping("/passwordModify")
    public GlobalResponse passwordModify(@RequestBody @Valid MemberPwModifyRequest memberPwModifyRequest) {
        memberService.passwordModify(memberPwModifyRequest.passwordModify(),
                memberPwModifyRequest.passwordModifyCheck());

        return GlobalResponse.of("200");
    }

    @PostMapping("/setProfileImage")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<MemberImageResponse> setProfileImage(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        Member member = memberService.getMemberByUsername(principal.getName());
        String oldImagePath = member.getImageUrl();

        MemberImageResponse memberImageResponse = profileImageService.uploadMemberImage(file);
        if (oldImagePath != null && !oldImagePath.equals(profileImageService.defaultProfileUrl)) {
            profileImageService.deleteFile(oldImagePath);
        }
        memberService.setProfileImage(member, memberImageResponse.profileImageUrl());

        return GlobalResponse.of(
                "200",
                memberImageResponse
        );
    }
}
