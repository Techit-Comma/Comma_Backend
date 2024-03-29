package com.bitharmony.comma.album.album.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bitharmony.comma.album.album.entity.Album;

public interface AlbumRepository extends JpaRepository<Album, Long>, AlbumRepositoryCustom{
	Optional<Album> findByAlbumname(String albumname);
	Optional<Album> findByFilePath(String filePath);

	Page<Album> findFirst20ByMemberUsernameOrderByIdDesc(String username, Pageable pageable);

	Page<Album> findFirst20ByOrderByIdDesc(Pageable pageable);
}