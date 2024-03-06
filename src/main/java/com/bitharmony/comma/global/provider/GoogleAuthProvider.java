package com.bitharmony.comma.global.provider;

import com.bitharmony.comma.global.exception.member.GetTokenFailureException;
import com.bitharmony.comma.member.dto.GoogleOauthResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
@RequiredArgsConstructor
public class GoogleAuthProvider {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.scope}")
    private String scope;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final String getCodeURL = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String getTokenURL = "https://oauth2.googleapis.com/token";

    public String generateRegisterUrl() {
        return getCodeURL
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope
                + "&response_type=code"
                + "&access_type=offline";
    }

    public GoogleOauthResponse getUserInfo(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> params = new HashMap<>();
        params.put("code", accessCode);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<GoogleOauthResponse> responseEntity = restTemplate.postForEntity(getTokenURL, params, GoogleOauthResponse.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new GetTokenFailureException();
        }

        return responseEntity.getBody();

    }

}
