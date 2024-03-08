package com.bitharmony.comma.album.album.dto;

import lombok.Builder;

@Builder
public record AlbumResponse(Long id,String albumname, String genre, boolean license, String licenseDescription, String imgUrl,
							String fileUrl, boolean permit, int price, String artistNickname, String artistUsername) {
}