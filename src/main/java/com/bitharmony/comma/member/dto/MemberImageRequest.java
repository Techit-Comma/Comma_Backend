package com.bitharmony.comma.member.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record MemberImageRequest(
        MultipartFile multipartFile
) {
}
