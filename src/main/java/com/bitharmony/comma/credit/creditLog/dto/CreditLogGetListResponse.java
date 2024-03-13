package com.bitharmony.comma.credit.creditLog.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;


@Builder
public record CreditLogGetListResponse(
        long restCredit,
        Page<CreditLogDto> creditLogDtos

        ) {
}
