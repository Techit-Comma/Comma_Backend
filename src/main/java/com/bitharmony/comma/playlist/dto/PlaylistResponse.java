package com.bitharmony.comma.playlist.dto;

import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record PlaylistResponse(
        Long playlistId,
        String title,
        String producerUsername,
        String producerNickname
) {

}
