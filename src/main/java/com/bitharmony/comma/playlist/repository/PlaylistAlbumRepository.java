package com.bitharmony.comma.playlist.repository;

import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.playlist.entity.PlayListAlbum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistAlbumRepository extends JpaRepository<PlayListAlbum, Long> {

    List<Album> findAllByPlaylistId(Long playlistId);
    void deleteByPlaylistId(Long playlistId);
}
