package com.bitharmony.comma.album.playlist.service;

import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.album.album.repository.AlbumRepository;
import com.bitharmony.comma.album.album.util.AlbumConvertUtil;
import com.bitharmony.comma.global.exception.member.NotAuthorizedException;
import com.bitharmony.comma.global.exception.playlist.PlaylistAlbumNotFoundException;
import com.bitharmony.comma.global.exception.playlist.PlaylistNotFoundException;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.album.playlist.dto.PlaylistDetailResponse;
import com.bitharmony.comma.album.playlist.dto.PlaylistResponse;
import com.bitharmony.comma.album.playlist.entity.PlayListAlbum;
import com.bitharmony.comma.album.playlist.entity.Playlist;
import com.bitharmony.comma.album.playlist.repository.PlaylistAlbumRepository;
import com.bitharmony.comma.album.playlist.repository.PlaylistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistAlbumRepository playlistAlbumRepository;
    private final AlbumRepository albumRepository;
    private final AlbumConvertUtil albumConvertUtil;

    @Transactional
    public void createPlaylist(String title, String description, Member member) {
        Playlist playlist = Playlist.builder()
                .title(title)
                .description(description)
                .producer(member)
                .build();

        playlistRepository.save(playlist);
    }

    @Transactional(readOnly = true)
    public List<PlaylistResponse> getAllPlaylist(Member member) {
        List<Playlist> playlists = playlistRepository.findAllByProducerId(member.getId());

        return playlists.stream()
                .map(playlist -> convertToPlaylistResponse(
                        playlist,
                        member.getUsername(),
                        member.getNickname()
                )).toList();
    }

    @Transactional(readOnly = true)
    public PlaylistDetailResponse getAlbumList(Long playlistId) {
        Playlist playlist = getPlaylistById(playlistId);
        List<PlayListAlbum> playListAlbums = playlistAlbumRepository.findAllByPlaylistId(playlistId);

        List<Long> albumIds = playListAlbums.stream()
                .map(playListAlbum -> playListAlbum.getAlbum().getId())
                .toList();

        List<Album> albums = albumRepository.findAllById(albumIds);

        return PlaylistDetailResponse.builder()
                .title(playlist.getTitle())
                .producerUsername(playlist.getProducer().getUsername())
                .producerNickname(playlist.getProducer().getNickname()) // TODO: Album 정보 넘길 시에도 같은 형식으로 넘기기에 리팩토링 필요
                .albumList(albums.stream().map(albumConvertUtil::convertToDto).toList())
                .build();
    }

    @Transactional
    public void modifyPlaylist(Long playlistId, String title, String description, Member member) {
        Playlist playlist = getPlaylistById(playlistId);
        checkPlaylistProducer(playlist.getProducer().getId(), member.getId());

        Playlist _playlist = playlist.toBuilder()
                .title(title)
                .description(description)
                .build();

        playlistRepository.save(_playlist);
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

    @Transactional(readOnly = true)
    public Playlist getPlaylistById(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(PlaylistNotFoundException::new);
    }

    public void checkPlaylistProducer(Long producerId, Long memberId) {
        if (!producerId.equals(memberId)) {
            throw new NotAuthorizedException();
        }
    }

    private PlaylistResponse convertToPlaylistResponse(Playlist playlist, String producerUsername, String producerNickname) {
        return PlaylistResponse.builder()
                .playlistId(playlist.getId())
                .title(playlist.getTitle())
                .producerUsername(producerNickname)
                .producerNickname(producerUsername)
                .build();
    }

}
