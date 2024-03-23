package com.bitharmony.comma.album.album.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitharmony.comma.album.album.dto.AlbumCreateRequest;
import com.bitharmony.comma.album.album.dto.AlbumEditRequest;
import com.bitharmony.comma.album.album.dto.AlbumFindRequest;
import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.global.exception.album.AlbumBuyException;
import com.bitharmony.comma.global.exception.album.AlbumFieldException;
import com.bitharmony.comma.global.exception.album.AlbumPermissionException;
import com.bitharmony.comma.album.album.service.AlbumLikeService;
import com.bitharmony.comma.album.album.service.AlbumService;
import com.bitharmony.comma.album.album.util.AlbumConvertUtil;
import com.bitharmony.comma.global.exception.member.MemberNotFoundException;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;
	private final AlbumLikeService albumLikeService;
	private final AlbumConvertUtil albumConvertUtil;
	private final MemberService memberService;

	@PostMapping("/release")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse releaseAlbum(@Valid AlbumCreateRequest request, Principal principal) {

		Member member = memberService.getMemberByUsername(principal.getName());

		if (!albumService.canRelease(request.albumname(), member)) {
			throw new AlbumFieldException();
		}

		Album album = albumService.release(request, member);
		return GlobalResponse.of("200", albumConvertUtil.albumToResponseDto(album));
	}

	@GetMapping("/detail/{id}")
	public GlobalResponse getAlbum(@PathVariable long id) {
		Album album = albumService.getAlbumById(id);

		return GlobalResponse.of("200", albumConvertUtil.albumToResponseDto(album));
	}

	@GetMapping("/{username}")
	public GlobalResponse getUserAlbumList(@PathVariable String username) {
		Member member = memberService.getMemberByUsername(username);

		if (member == null) {
			throw new MemberNotFoundException();
		}

		 Page<AlbumListResponse> albumPage = albumService.getLatest20Albums(username);
		return GlobalResponse.of("200", albumPage);
	}

	@GetMapping("/list")
	public GlobalResponse getAlbumList() {
		Page<AlbumListResponse> albumPage = albumService.getLatest20Albums(null);
		return GlobalResponse.of("200", albumPage);
	}

	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse editAlbum(@PathVariable long id, @Valid AlbumEditRequest request, Principal principal) {
		Album album = albumService.getAlbumById(id);
		Member member = memberService.getMemberByUsername(principal.getName());

		if (!albumService.canEdit(album, principal, request, member)) {
			throw new AlbumFieldException();
		}

		Album editedAlbum = albumService.edit(request, album);
		return GlobalResponse.of("200", albumConvertUtil.albumToResponseDto(editedAlbum));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse deleteAlbum(@PathVariable long id, Principal principal) {
		Album album = albumService.getAlbumById(id);

		if (!albumService.canDelete(album, principal)) {
			throw new AlbumPermissionException();
		}

		albumService.delete(album);
		return GlobalResponse.of("200");
	}

	@GetMapping("/{albumId}/like")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse getLike(@PathVariable long albumId, Principal principal) {
		Member member = memberService.getMemberByUsername(principal.getName());
		Album album = albumService.getAlbumById(albumId);

		return GlobalResponse.of("200", albumLikeService.canLike(member, album));
	}

	@PostMapping("/{albumId}/like")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse like(@PathVariable long albumId, Principal principal) {
		Member member = memberService.getMemberByUsername(principal.getName());
		Album album = albumService.getAlbumById(albumId);

		if (!albumLikeService.canLike(member, album)) {
			throw new AlbumPermissionException();
		}

		albumLikeService.like(member, album);
		return GlobalResponse.of("200");
	}

	@PostMapping("/{albumId}/cancelLike")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse cancelLike(@PathVariable long albumId, Principal principal) {
		Member member = memberService.getMemberByUsername(principal.getName());
		Album album = albumService.getAlbumById(albumId);

		if (!albumLikeService.canCancelLike(member, album)) {
			throw new AlbumPermissionException();
		}

		albumLikeService.like(member, album);
		return GlobalResponse.of("200");
	}

	@PostMapping("/{albumId}/buy")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse buyAlbum(@PathVariable long albumId, Principal principal) {
		Member member = memberService.getMemberByUsername(principal.getName());
		Album album = albumService.getAlbumById(albumId);

		if (!albumService.canBuy(member, album)) {
			throw new AlbumBuyException();
		}

		memberService.updateUserAlbum(member, album);
		return GlobalResponse.of("200");
	}

	@GetMapping("/myAlbum")
	@PreAuthorize("isAuthenticated()")
		public GlobalResponse getMyAlbum(Principal principal) {
		Member member = memberService.getMemberByUsername(principal.getName());
		return GlobalResponse.of("200", member.getAlbumList().stream().map(albumConvertUtil::albumToResponseDto).toList());
	}

	@PostMapping("/{albumId}/streaming")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse streaming(@PathVariable long albumId, Principal principal) {
		Member member = memberService.getMemberByUsername(principal.getName());
		Album album = albumService.getAlbumById(albumId);

		albumService.streamingMember(member, album);
		return GlobalResponse.of("200");
	}

	@GetMapping("/streamingTop10Albums")
	public GlobalResponse getTop10Albums() {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("id"));
		Pageable pageable = PageRequest.of(0, 10, Sort.by(sorts));

		Page<Album> itemsPage = albumService.streamingTop10Albums(pageable);
		return GlobalResponse.of("200", itemsPage.map(albumConvertUtil::albumToResponseDto).toList());
	}

	@GetMapping("/recommendAlbum")
	public GlobalResponse getRecommendAlbum(Principal principal) {
		Optional<Member> memberOpt = Optional.empty();

		if (principal != null) {
			memberOpt = Optional.ofNullable(memberService.getMemberByUsername(principal.getName()));
		}

		Sort sort = Sort.by(Sort.Direction.DESC, "id");
		Pageable pageable = PageRequest.of(0, 10, sort);

		Page<Album> itemsPage = albumService.musicRecommendation10Albums(principal, memberOpt.orElse(null), pageable);
		return GlobalResponse.of("200", itemsPage.map(albumConvertUtil::albumToResponseDto).toList());
	}


	@GetMapping("/searchAlbum")
	public GlobalResponse searchAlbum(@Valid AlbumFindRequest request) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("id"));
		Pageable pageable = PageRequest.of(request.page() - 1, 10, Sort.by(sorts));

		Page<Album> itemsPage = albumService.search(request, pageable);
		return GlobalResponse.of("200", itemsPage.map(albumConvertUtil::albumToResponseDto).toList());
	}
}
