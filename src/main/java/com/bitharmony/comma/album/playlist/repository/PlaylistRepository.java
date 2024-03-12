package com.bitharmony.comma.album.playlist.repository;

import com.bitharmony.comma.album.playlist.entity.Playlist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByProducerId(Long producerId);
}
