package com.bitharmony.comma.album.album.dto;

import lombok.Builder;

@Builder
public record AlbumListResponse(Long id, String albumname,
								String genre,
								String fileUrl,
								String imgUrl,
								String artistNickname,
								String artistUsername) {
}