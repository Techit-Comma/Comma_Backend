package com.bitharmony.comma.global.util;

import com.bitharmony.comma.global.exception.member.ExpiredAccessTokenException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.bitharmony.comma.member.auth.dto.JwtCreateRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${secret.jwt.secret}")
    private String SECRET_KEY;
    @Value("${secret.jwt.token.access-expiration-time}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME; // 1시간으로 설정
    @Value("${secret.jwt.token.refresh-expiration-time}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME; // 7일로 설정

    public String createAccessToken(JwtCreateRequest jwtCreateRequest) {

        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(jwtCreateRequest.id()));
        data.put("username", jwtCreateRequest.username());

        Claims claims = Jwts
            .claims()
            .add("data", data)
            .build();

        return Jwts.builder()
            .claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(getKey())
            .compact();
    }

    public String createRefreshToken(JwtCreateRequest jwtCreateRequest) {
        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(jwtCreateRequest.id()));
        data.put("username", jwtCreateRequest.username());

        Claims claims = Jwts
            .claims()
            .add("data", data)
            .build();

        String refreshToken = Jwts.builder()
            .claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(getKey())
            .compact();

        // redis에 저장
        redisTemplate.opsForValue().set(
            jwtCreateRequest.username(),
            refreshToken,
            REFRESH_TOKEN_EXPIRATION_TIME,
            TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    private Claims getClaim(String token) {
        return Jwts
            .parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validToken(String token) {
        try {
            getClaim(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredAccessTokenException();
        } catch (JwtException e) {
            return false;
        }
    }

    public Map<String, String> getUserData(String token) {
        Claims claim = getClaim(token);
        Map<String, String> data = (Map<String, String>) claim.get("data");
        return data;
    }

}