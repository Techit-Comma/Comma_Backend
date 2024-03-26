package com.bitharmony.comma.member.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MemberPwModifyRequest(
        @NotBlank(message = "기존 비밀번호를 입력해주세요.") String password,
        @NotBlank(message = "새 비밀번호를 입력해주세요.") String newPassword,
        @NotBlank(message = "새 비밀번호를 다시 한 번 입력해주세요.") String newPasswordCheck
) {
}
