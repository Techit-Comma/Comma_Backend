package com.bitharmony.comma.album.album.dto;

import lombok.Builder;

@Builder
public record AlbumFindRequest(String kwTypes,
							   String kw,
							   int page) {
}