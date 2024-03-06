package com.bitharmony.comma.member.service;

import com.bitharmony.comma.global.exception.MemberNotFoundException;
import com.bitharmony.comma.global.exception.member.MemberInfoMappingException;
import com.bitharmony.comma.global.provider.GoogleAuthProvider;
import com.bitharmony.comma.global.util.JwtUtil;
import com.bitharmony.comma.member.dto.GoogleMemberInfo;
import com.bitharmony.comma.member.dto.GoogleOauthResponse;
import com.bitharmony.comma.member.dto.JwtCreateRequest;
import com.bitharmony.comma.member.dto.MemberLoginResponse;
import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.Decoders;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final MemberRepository memberRepository;
    private final GoogleAuthProvider googleAuthProvider;
    private final JwtUtil jwtUtil;

    public String getRegisterUrl() {
        return googleAuthProvider.generateRegisterUrl();
    }

    @Transactional
    public MemberLoginResponse login(String accessCode) {
        GoogleMemberInfo googleMemberInfo = getGoogleMemberInfo(accessCode);

        if (memberRepository.findByProviderId(googleMemberInfo.sub()).isEmpty()) { // 회원 정보가 없다면, 등록 선 진행
            memberRepository.save(googleMemberInfo.toEntity());
        }

        Member member = memberRepository.findByProviderId(googleMemberInfo.sub())
                .orElseThrow(MemberNotFoundException::new);

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


    private GoogleMemberInfo getGoogleMemberInfo(String accessCode) {
        GoogleOauthResponse googleOauthResponse = googleAuthProvider.getUserInfo(accessCode);
        String userInfo = decryptBase64Token(googleOauthResponse.id_token().split("\\.")[1]);

        return transJsonToMember(userInfo);
    }

    private String decryptBase64Token(String jwtToken) {
        byte[] decode = Decoders.BASE64URL.decode(jwtToken);
        return new String(decode, StandardCharsets.UTF_8);
    }

    private GoogleMemberInfo transJsonToMember(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return objectMapper.readValue(json, GoogleMemberInfo.class);

        } catch (JsonProcessingException e) {
            throw new MemberInfoMappingException();
        }
    }
}
