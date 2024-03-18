package com.bitharmony.comma.global.exception;

import com.bitharmony.comma.global.exception.member.ExpiredAccessTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        Object exception = request.getAttribute("exception");

        if (exception instanceof ExpiredAccessTokenException tokenException) {
            setResponse(response, tokenException.getStatusCode(), tokenException.getMessage());
            return;
        }

        String errorMessage = authException.getMessage();
        setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }

    public void setResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=UTF-8");

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", message);
        responseJson.put("code", statusCode);
        response.getWriter().print(responseJson);
    }
}
