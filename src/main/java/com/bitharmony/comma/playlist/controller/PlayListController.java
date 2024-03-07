package com.bitharmony.comma.playlist.controller;

import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.member.service.MemberService;
import com.bitharmony.comma.playlist.dto.PlaylistRequest;
import com.bitharmony.comma.playlist.service.PlaylistService;
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public void createPlaylist(PlaylistRequest playlistRequest, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        playlistService.createPlaylist(playlistRequest.title(), member);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{playlistId}")
    public GlobalResponse<List<AlbumListResponse>> getPlaylist(@PathVariable Long playlistId) {
        return GlobalResponse.of("200", playlistService.getAlbumList(playlistId));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{playlistId}")
    public void modifyPlaylistInfo(@PathVariable Long playlistId, PlaylistRequest playlistRequest) {
       playlistService.modifyPlaylist(playlistId, playlistRequest.title());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{playlistId}")
    public void deletePlaylist(@PathVariable Long playlistId) {
        playlistService.deletePlaylist(playlistId);
    }

}
