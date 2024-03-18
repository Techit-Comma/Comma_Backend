package com.bitharmony.comma.album.playlist.repository;

import com.bitharmony.comma.album.playlist.entity.PlayListAlbum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistAlbumRepository extends JpaRepository<PlayListAlbum, Long> {

    List<PlayListAlbum> findAllByPlaylistId(Long playlistId);
    Optional<PlayListAlbum> findByPlaylistIdAndAlbumId(Long playlistId, Long albumId);
    void deleteByPlaylistId(Long playlistId);
}
