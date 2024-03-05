package com.bitharmony.comma.member.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record MemberImageResponse(
        String originalFileName,
        String uploadFileName,
        String uploadFileUrl
) {
}
