package com.bitharmony.comma.album.album.repository;

import com.bitharmony.comma.member.member.entity.Member;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bitharmony.comma.album.album.entity.Album;

public interface AlbumRepositoryCustom{
	Page<Album> search(List<String> kwTypes, String kw, Pageable pageable);
	Page<Album> streamingTop10Albums(Pageable pageable);
	Page<Album> musicRecommendation10Albums(Principal principal, Member member, Pageable pageable);
}