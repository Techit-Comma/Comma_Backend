package com.bitharmony.comma.album.Image.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bitharmony.comma.album.Image.service.AlbumImageService;
import com.bitharmony.comma.album.album.service.AlbumService;
import com.bitharmony.comma.album.file.service.FileService;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumImageController {

	private final MemberService memberService;
	private final AlbumService albumService;
	private final AlbumImageService albumImageService;
	private final FileService fileService;

	@PostMapping("/upload")
	@PreAuthorize("isAuthenticated()")
	public GlobalResponse releaseAlbum(@RequestParam(value = "musicImageFile", required = false) MultipartFile musicImageFile, Principal principal) {

		if(!albumImageService.checkImageFile(musicImageFile)) return GlobalResponse.of("400", "파일 형식이 잘못되었습니다.");
		return GlobalResponse.of("200", albumImageService.uploadAlbumImage(musicImageFile).uploadFileUrl());
	}

	@GetMapping("/status")
	public GlobalResponse encodeStatus(@RequestParam(value = "filePath", required = true) String filePath) {
		return GlobalResponse.of("200", fileService.isFileUploadedToS3(filePath));
	}
}