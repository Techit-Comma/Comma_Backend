package com.bitharmony.comma.album.album.service;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitharmony.comma.album.Image.service.AlbumImageService;
import com.bitharmony.comma.album.album.dto.AlbumCreateRequest;
import com.bitharmony.comma.album.album.dto.AlbumEditRequest;
import com.bitharmony.comma.album.album.dto.AlbumListResponse;
import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.album.album.exception.AlbumNotFoundException;
import com.bitharmony.comma.album.album.repository.AlbumRepository;
import com.bitharmony.comma.album.file.service.FileService;
import com.bitharmony.comma.album.file.util.NcpImageUtil;
import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.streaming.util.NcpMusicUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {
	private final AlbumImageService albumImageService;
	private final AlbumRepository albumRepository;
	private final FileService fileService;
	private final NcpImageUtil ncpImageUtil;
	private final NcpMusicUtil ncpMusicUtil;

	@Transactional
	public Album release(AlbumCreateRequest request, Member member) {
		Album album = request.toEntity();
		album.updateReleaseMember(member);
		return saveAlbum(album);
	}

	@Transactional
	public Album edit(AlbumEditRequest request, Album album) {
		album.update(request);

		if (album.getFilePath() != null)
			fileService.deleteFile(fileService.getAlbumFileUrl(album.getImagePath()), ncpImageUtil.getBucketName());

		saveAlbum(album);
		return album;
	}

	@Transactional
	public void delete(Album album) {
		ncpMusicUtil.deleteFile(album.getFilePath());
		fileService.deleteFile(album.getImagePath(), ncpImageUtil.getBucketName());
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

		return albums.map(this::convertToDto);
	}

	public AlbumListResponse convertToDto(Album album) {
		return AlbumListResponse.builder()
			.id(album.getId())
			.albumname(album.getAlbumname())
			.genre(album.getGenre())
			.imgPath(getAlbumImageUrl(album.getImagePath()))
			.permit(album.isPermit())
			.price(album.getPrice())
			.artistUsername(album.getMember().getUsername())
			.artistNickname(album.getMember().getNickname())
			.build();
	}

	private String replaceBucketName(String filepath, String bucketName, String replacement) {
		return filepath.replace(bucketName, replacement);
	}

	public String getAlbumImageUrl(String filepath) {
		if (filepath == null) {
			return null;
		}

		return ncpImageUtil.getImageCdn() + replaceBucketName(filepath, ncpImageUtil.getBucketName(), "")
			+ ncpImageUtil.getImageCdnQueryString();
	}

	public boolean canRelease(String name, Member member) {
		if (member == null)
			return false;
		if (albumRepository.findByAlbumname(name).isPresent())
			return false;

		return true;
	}

	public boolean canEdit(Album album, Principal principal, @Valid AlbumEditRequest request, Member member) {
		if (member == null)
			return false;
		if (!album.getMember().getUsername().equals(principal.getName()))
			return false;
		if (albumRepository.findByAlbumname(request.albumname()).isPresent() && !album.getAlbumname().equals(request.albumname()))
			return false;

		return true;
	}

	public boolean canDelete(Album album, Principal principal) {
		if (!album.getMember().getUsername().equals(principal.getName()))
			return false;
		return true;
	}
}