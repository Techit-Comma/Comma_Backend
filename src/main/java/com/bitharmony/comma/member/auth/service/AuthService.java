package com.bitharmony.comma.member.auth.service;

import com.bitharmony.comma.global.util.JwtUtil;
import com.bitharmony.comma.member.auth.dto.JwtCreateRequest;
import com.bitharmony.comma.member.member.dto.MemberLoginResponse;
import com.bitharmony.comma.member.auth.dto.JwtRegenerateRequest;
import com.bitharmony.comma.global.exception.member.InvalidRefreshTokenException;
import com.bitharmony.comma.global.exception.member.RefreshTokenNotMatchException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public MemberLoginResponse reissue(JwtRegenerateRequest jwtRegenerateRequest) {

        String refreshToken = jwtRegenerateRequest.refreshToken();

        if (!jwtUtil.validToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        Map<String, String> userData = jwtUtil.getUserData(refreshToken);

        String getRefreshToken = redisTemplate.opsForValue().get(userData.get("username"));
        if (!getRefreshToken.equals(refreshToken)) {
            throw new RefreshTokenNotMatchException();
        }

        JwtCreateRequest jwtCreateRequest = JwtCreateRequest.builder()
                .id(Long.parseLong(userData.get("id")))
                .username(userData.get("username"))
                .build();

        String newRefreshToken = jwtUtil.createRefreshToken(jwtCreateRequest);

        return MemberLoginResponse.builder()
                .accessToken(jwtUtil.createAccessToken(jwtCreateRequest))
                .refreshToken(newRefreshToken)
                .build();
    }
}
