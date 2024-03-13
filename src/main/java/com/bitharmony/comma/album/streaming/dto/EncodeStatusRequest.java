package com.bitharmony.comma.album.streaming.dto;

import com.bitharmony.comma.album.streaming.util.EncodeStatus;

public record EncodeStatusRequest(
        int categoryId,
        String categoryName,
        int encodingOptionId,
        int fileId,
        String filePath,
        String outputType,
        EncodeStatus status
) {

}
