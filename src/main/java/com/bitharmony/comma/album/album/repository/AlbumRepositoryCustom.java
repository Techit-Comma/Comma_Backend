package com.bitharmony.comma.album.album.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bitharmony.comma.album.album.entity.Album;

public interface AlbumRepositoryCustom{
	Page<Album> search(List<String> kwTypes, String kw, Pageable pageable);
}