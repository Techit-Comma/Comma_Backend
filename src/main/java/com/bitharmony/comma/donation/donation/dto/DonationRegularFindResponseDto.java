package com.bitharmony.comma.donation.donation.dto;

import lombok.Builder;

@Builder
public record DonationRegularFindResponseDto(
        String patronUsername,
        String artistUsername,
        Long amount,
        Integer executeDay,
        boolean anonymous
){}
