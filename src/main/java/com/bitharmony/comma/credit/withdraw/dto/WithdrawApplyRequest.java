package com.bitharmony.comma.credit.withdraw.dto;

import lombok.Builder;

@Builder
public record WithdrawApplyRequest(
        String bankName,
        String bankAccountNo,
        long withdrawAmount
) {

}
