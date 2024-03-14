package com.bitharmony.comma.member.auth.service;

import com.bitharmony.comma.global.exception.member.MemberNotFoundException;
import com.bitharmony.comma.global.exception.member.MemberInfoMappingException;
import com.bitharmony.comma.member.auth.provider.GithubAuthProvider;
import com.bitharmony.comma.member.auth.provider.GoogleAuthProvider;
import com.bitharmony.comma.global.util.JwtUtil;
import com.bitharmony.comma.member.auth.dto.GithubMemberResponse;
import com.bitharmony.comma.member.auth.dto.GithubOauthResponse;
import com.bitharmony.comma.member.auth.dto.GoogleMemberResponse;
import com.bitharmony.comma.member.auth.dto.GoogleOauthResponse;
import com.bitharmony.comma.member.auth.dto.JwtCreateRequest;
import com.bitharmony.comma.member.member.dto.MemberLoginResponse;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.Decoders;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final GoogleAuthProvider googleAuthProvider;
    private final GithubAuthProvider githubAuthProvider;
    private final JwtUtil jwtUtil;

    public String getGoogleRegisterUrl() {
        return googleAuthProvider.generateRegisterUrl();
    }

    public String getGithubRegisterUrl() {
        return githubAuthProvider.generateRegisterUrl();
    }

    @Transactional
    public MemberLoginResponse googleLogin(String accessCode) {
        GoogleMemberResponse googleMemberResponse = getGoogleMemberInfo(accessCode);
        Member member = getMemberByProviderId(googleMemberResponse.sub(), googleMemberResponse.toEntity());

        return getMemberLoginResponse(member);
    }

    @Transactional
    public MemberLoginResponse githubLogin(String accessCode) {
        GithubMemberResponse githubMemberResponse = getGithubMemberInfo(accessCode);
        Member member = getMemberByProviderId(githubMemberResponse.id(), githubMemberResponse.toEntity());

        return getMemberLoginResponse(member);
    }

    private MemberLoginResponse getMemberLoginResponse(Member member) {
        JwtCreateRequest jwtCreateRequest = JwtCreateRequest.builder()
                .id(member.getId())
                .username(member.getUsername())
                .build();

        String accessToken = jwtUtil.createAccessToken(jwtCreateRequest);
        String refreshToken = jwtUtil.createRefreshToken(jwtCreateRequest);

        return MemberLoginResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public Member getMemberByProviderId(String ProviderId, Member memberToEntity) {
        Optional<Member> member = memberRepository.findByProviderId(ProviderId);

        if (member.isEmpty()) { // 회원 정보가 없다면, 등록 선 진행
            memberRepository.save(memberToEntity);
            return memberRepository.findByProviderId(ProviderId)
                    .orElseThrow(MemberNotFoundException::new);
        }

        return member.get();
    }

    private GithubMemberResponse getGithubMemberInfo(String accessCode) {
        GithubOauthResponse githubOauthResponse = githubAuthProvider.getAccessToken(accessCode);
        return githubAuthProvider.getMemberInfo(githubOauthResponse.access_token());
    }

    private GoogleMemberResponse getGoogleMemberInfo(String accessCode) {
        GoogleOauthResponse googleOauthResponse = googleAuthProvider.getUserInfo(accessCode);
        String userInfo = decryptBase64Token(googleOauthResponse.id_token().split("\\.")[1]);

        return transJsonToMember(userInfo);
    }

    private String decryptBase64Token(String jwtToken) {
        byte[] decode = Decoders.BASE64URL.decode(jwtToken);
        return new String(decode, StandardCharsets.UTF_8);
    }

    private GoogleMemberResponse transJsonToMember(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return objectMapper.readValue(json, GoogleMemberResponse.class);

        } catch (JsonProcessingException e) {
            throw new MemberInfoMappingException();
        }
    }
}
