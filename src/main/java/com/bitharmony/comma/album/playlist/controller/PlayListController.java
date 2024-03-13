package com.bitharmony.comma.album.playlist.controller;

import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.album.album.service.AlbumService;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.service.MemberService;
import com.bitharmony.comma.album.playlist.dto.PlaylistAlbumRequest;
import com.bitharmony.comma.album.playlist.dto.PlaylistRequest;
import com.bitharmony.comma.album.playlist.dto.PlaylistDetailResponse;
import com.bitharmony.comma.album.playlist.dto.PlaylistResponse;
import com.bitharmony.comma.album.playlist.service.PlaylistService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlayListController {

    private final PlaylistService playlistService;
    private final MemberService memberService;
    private final AlbumService albumService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public void createPlaylist(PlaylistRequest playlistRequest, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        playlistService.createPlaylist(playlistRequest.title(), member);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public GlobalResponse<List<PlaylistResponse>> getAllPlaylist(Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        return GlobalResponse.of("200", playlistService.getAllPlaylist(member));
    }

    @GetMapping("/{playlistId}")
    public GlobalResponse<PlaylistDetailResponse> getPlaylist(@PathVariable Long playlistId) {
        return GlobalResponse.of("200", playlistService.getAlbumList(playlistId));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{playlistId}")
    public void modifyPlaylistInfo(@PathVariable Long playlistId, PlaylistRequest playlistRequest,
            Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        playlistService.modifyPlaylist(playlistId, playlistRequest.title(), member);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{playlistId}")
    public void deletePlaylist(@PathVariable Long playlistId, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        playlistService.deletePlaylist(playlistId, member);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{playlistId}/album")
    public void addAlbumToPlaylist(@PathVariable Long playlistId,
            PlaylistAlbumRequest playlistAlbumRequest) {
        Album album = albumService.getAlbumById(playlistAlbumRequest.albumId());
        playlistService.addPlaylistAlbum(playlistId, album);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{playlistId}/album")
    public void deleteAlbumToPlaylist(@PathVariable Long playlistId,
            PlaylistAlbumRequest playlistAlbumRequest, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        playlistService.deletePlaylistAlbum(playlistId, playlistAlbumRequest.albumId(), member);
    }


}
