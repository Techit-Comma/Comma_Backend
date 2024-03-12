package com.bitharmony.comma.file.dto;

import lombok.Builder;

@Builder
public record FileResponse(String originalFileName,
                           String uploadFileName,
                           String uploadFilePath,
                           String uploadFileUrl) {
}