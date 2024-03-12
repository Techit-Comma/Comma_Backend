package com.bitharmony.comma.global.response;

import java.util.Map;
import lombok.Builder;

@Builder
public record ErrorResponse(
        String code,
        String message,
        Map<String, String> validMessages)
{

}