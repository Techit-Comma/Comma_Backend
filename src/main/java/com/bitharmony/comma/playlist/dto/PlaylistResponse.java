package com.bitharmony.comma.playlist.dto;

import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record PlaylistResponse(
        String title,
        String producerUsername,
        String producerNickname,
        List<AlbumListResponse> albumList
) {

}
