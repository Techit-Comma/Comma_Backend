package com.bitharmony.comma.playlist.service;

import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.album.album.repository.AlbumRepository;
import com.bitharmony.comma.album.album.service.AlbumService;
import com.bitharmony.comma.global.exception.NotAuthorizedException;
import com.bitharmony.comma.global.exception.playlist.PlaylistAlbumNotFoundException;
import com.bitharmony.comma.global.exception.playlist.PlaylistNotFoundException;
import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.playlist.entity.PlayListAlbum;
import com.bitharmony.comma.playlist.entity.Playlist;
import com.bitharmony.comma.playlist.repository.PlaylistAlbumRepository;
import com.bitharmony.comma.playlist.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistAlbumRepository playlistAlbumRepository;

    private final AlbumService albumService; // TODO: 리팩토링 필요

    @Transactional
    public void createPlaylist(String title, Member member) {
        Playlist playlist = Playlist.builder()
                .title(title)
                .producer(member)
                .build();

        playlistRepository.save(playlist);
    }

    @Transactional
    public List<AlbumListResponse> getAlbumList(Long playlistId) {
        List<Album> playListAlbums = playlistAlbumRepository.findAllByPlaylistId(playlistId);
        return playListAlbums.stream().map(albumService::convertToDto).toList();
    }

    @Transactional
    public void modifyPlaylist(Long playlistId, String title, Member member) {
        Playlist playlist = getPlaylistById(playlistId);
        checkPlaylistProducer(playlist.getProducer().getId(), member.getId());

        playlist.toBuilder()
                .title(title);

        playlistRepository.save(playlist);
    }

    @Transactional
    public void deletePlaylist(Long playlistId, Member member) {
        Playlist playlist = getPlaylistById(playlistId);
        checkPlaylistProducer(playlist.getProducer().getId(), member.getId());

        playlistAlbumRepository.deleteByPlaylistId(playlistId);
        playlistRepository.delete(playlist);
    }

    @Transactional
    public void addPlaylistAlbum(Long playlistId, Album album) {
        Playlist playlist = getPlaylistById(playlistId);

        PlayListAlbum playListAlbum = PlayListAlbum.builder()
                .playlist(playlist)
                .album(album)
                .build();

        playlistAlbumRepository.save(playListAlbum);
    }

    @Transactional
    public void deletePlaylistAlbum(Long playlistId, Long albumId, Member member) {
        Playlist playlist = getPlaylistById(playlistId);
        checkPlaylistProducer(playlist.getProducer().getId(), member.getId());

        PlayListAlbum playListAlbum = playlistAlbumRepository.findByPlaylistIdAndAlbumId(playlistId, albumId)
                .orElseThrow(PlaylistAlbumNotFoundException::new);

        playlistAlbumRepository.deleteById(playListAlbum.getId());
    }

    @Transactional
    public Playlist getPlaylistById(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(PlaylistNotFoundException::new);
    }

    @Transactional
    public void checkPlaylistProducer(Long producerId, Long memberId) {
        if (!producerId.equals(memberId)) {
            throw new NotAuthorizedException();
        }
    }

}
