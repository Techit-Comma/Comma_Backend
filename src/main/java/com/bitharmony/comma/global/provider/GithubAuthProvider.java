package com.bitharmony.comma.global.provider;

import com.bitharmony.comma.global.exception.member.GetTokenFailureException;
import com.bitharmony.comma.member.dto.GithubMemberResponse;
import com.bitharmony.comma.member.dto.GithubOauthResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
@RequiredArgsConstructor
public class GithubAuthProvider {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.scope}")
    private String scope;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    private final String getCodeURL = "https://github.com/login/oauth/authorize";
    private final String getTokenURL = "https://github.com/login/oauth/access_token";
    private final String getUserURL = "https://api.github.com/user";

    public String generateRegisterUrl() {
        return getCodeURL
                + "?client_id=" + clientId
                + "&scope=" + scope;
    }

    public GithubOauthResponse getAccessToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> params = new HashMap<>();
        params.put("code", accessCode);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);

        ResponseEntity<GithubOauthResponse> responseEntity = restTemplate.postForEntity(getTokenURL, params, GithubOauthResponse.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new GetTokenFailureException();
        }

        return responseEntity.getBody();
    }

    public GithubMemberResponse getMemberInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<GithubMemberResponse> responseEntity = restTemplate.exchange(getUserURL, HttpMethod.GET, entity, GithubMemberResponse.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new GetTokenFailureException();
        }

        return responseEntity.getBody();
    }

}
