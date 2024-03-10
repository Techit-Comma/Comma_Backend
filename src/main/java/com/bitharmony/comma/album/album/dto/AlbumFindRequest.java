package com.bitharmony.comma.album.album.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;

@Builder
public record AlbumFindRequest(List<String> kwTypes, String kw, int page) {
	public AlbumFindRequest {
		if (kwTypes == null) {
			kwTypes = new ArrayList<>();
			kwTypes.add("albumName"); // 기본값 설정
		}
		if (kw == null) {
			kw = ""; // 기본값 설정
		}
		if (page <= 0) {
			page = 1; // 기본값 설정
		}
	}
}