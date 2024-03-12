package com.bitharmony.comma.donation.donation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public record DonationRegularRequestDto(
        @NotEmpty
        String patronName,

        @NotEmpty
        String artistName,

        @NotEmpty
        Long amount,

        @NotEmpty
        Integer executeDay,

        boolean anonymous
) {}
