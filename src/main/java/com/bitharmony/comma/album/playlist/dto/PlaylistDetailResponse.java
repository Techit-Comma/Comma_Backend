package com.bitharmony.comma.album.playlist.dto;

import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record PlaylistDetailResponse(
        String title,
        String description,
        String producerUsername,
        String producerNickname,
        List<AlbumListResponse> albumList
) {

}
