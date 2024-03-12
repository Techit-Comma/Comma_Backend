package com.bitharmony.comma.credit.withdraw.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WithdrawGetResponse(
        long id,
        String applicantName,
        String bankName,
        String bankAcountNo,
        long withdrawAmount,
        LocalDateTime applyDate,
        LocalDateTime withdrawCancelDate,
        LocalDateTime withdrawDoneDate

) {

}
