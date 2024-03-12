package com.bitharmony.comma.global.response;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public record ErrorResponse(
        String code,
        String message,
        Map<String, String> validMessages)
{

}