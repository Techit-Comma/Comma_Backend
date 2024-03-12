package com.bitharmony.comma.credit.withdraw.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record WithdrawGetListResponse(
        Page<WithdrawDto> withdraws
) {

}
