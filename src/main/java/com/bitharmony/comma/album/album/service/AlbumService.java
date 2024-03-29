package com.bitharmony.comma.album.album.service;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitharmony.comma.album.album.dto.AlbumCreateRequest;
import com.bitharmony.comma.album.album.dto.AlbumEditRequest;
import com.bitharmony.comma.album.album.dto.AlbumFindRequest;
import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.album.album.repository.AlbumRepository;
import com.bitharmony.comma.album.album.util.AlbumConvertUtil;
import com.bitharmony.comma.album.album.util.NcpImageUtil;
import com.bitharmony.comma.album.streaming.util.NcpMusicUtil;
import com.bitharmony.comma.file.service.FileService;
import com.bitharmony.comma.global.exception.album.AlbumNotFoundException;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.notification.service.NotificationService;
import com.bitharmony.comma.member.notification.util.NotificationType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumService {
	private final AlbumRepository albumRepository;
	private final FileService fileService;
	private final NotificationService notificationService;
	private final NcpImageUtil ncpImageUtil;
	private final NcpMusicUtil ncpMusicUtil;
	private final AlbumConvertUtil albumConvertUtil;

	@Transactional
	public Album release(AlbumCreateRequest request, Member member) {
		Album album = request.toEntity();
		album.updateReleaseMember(member);
		saveAlbum(album);

		notificationService.sendArtistNotification(member, NotificationType.NEW_ALBUM, album.getId()); // 알림 발송
		return album;
	}

	@Transactional
	public Album edit(AlbumEditRequest request, Album album) {
		album.update(request);

		if (album.getFilePath() != null)
			fileService.deleteFile(album.getImagePath(), fileService.getFileName(album.getImagePath(), ncpImageUtil.getBucketName()));

		saveAlbum(album);
		return album;
	}

	@Transactional
	public void delete(Album album) {
		// ncpMusicUtil.deleteFile(album.getFilePath());
		// fileService.deleteFile(album.getImagePath(), fileService.getFileName(album.getImagePath(), ncpImageUtil.getBucketName()));
		albumRepository.delete(album);
	}

	public Album saveAlbum(Album album) {
		albumRepository.save(album);
		return album;
	}

	public Album getAlbumById(long id) {
		return albumRepository.findById(id).orElseThrow(AlbumNotFoundException::new);
	}

	public Page<AlbumListResponse> getLatest20Albums(String username) {
		Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
		Page<Album> albums = Optional.ofNullable(username)
			.map(u -> albumRepository.findFirst20ByMemberUsernameOrderByIdDesc(u, topTwenty))
			.orElse(albumRepository.findFirst20ByOrderByIdDesc(topTwenty));

		return albums.map(albumConvertUtil::convertToDto);
	}

	public Page<Album> search(AlbumFindRequest request, Pageable pageable) {
		return albumRepository.search(request.kwTypes(), request.kw(), pageable);
	}

	public boolean canRelease(String name, Member member) {
		if (member == null)
			return false;
        return albumRepository.findByAlbumname(name).isEmpty();
    }

	public boolean canEdit(Album album, Principal principal, @Valid AlbumEditRequest request, Member member) {
		if (member == null)
			return false;
		if (!album.getMember().getUsername().equals(principal.getName()))
			return false;
        return albumRepository.findByAlbumname(request.albumname()).isEmpty()
                || album.getAlbumname().equals(request.albumname());
    }

	public boolean canDelete(Album album, Principal principal) {
        return album.getMember().getUsername().equals(principal.getName());
    }

	public boolean canBuy(Member member, Album album) {
		if (member.getCredit() < album.getPrice())
			return false;
		if (album.getMember().getUsername().equals(member.getUsername()))
			return false;
        return album.isPermit();
    }

	@Transactional
	public void resetStreamingCounts() {
		log.info("Resetting streaming counts");
		albumRepository.findAll().forEach(album -> {
			album.getStreamingCounts().clear();
			albumRepository.save(album);
		});
	}

	public Page<Album> musicRecommendation10Albums(Principal principal, Member member, Pageable pageable) {
		return albumRepository.musicRecommendation10Albums(principal, member, pageable);
	}

	public Page<Album> streamingTop10Albums(Pageable pageable) {
		return albumRepository.streamingTop10Albums(pageable);
	}

	public void streamingMember(Member member, Album album) {
		if(!album.getStreamingCounts().contains(member)) {
			album.getStreamingCounts().add(member);
			albumRepository.save(album);
		}
	}
}