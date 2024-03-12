package com.bitharmony.comma.album.playlist.dto;

import lombok.Builder;

@Builder
public record PlaylistResponse(
        Long playlistId,
        String title,
        String producerUsername,
        String producerNickname
) {

}
