package com.bitharmony.comma.member.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MemberModifyRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(message = "닉네임은 5글자 이상 100자 이하여야 합니다.", min = 5, max = 100)
        String nickname,
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        String email
) {
}
