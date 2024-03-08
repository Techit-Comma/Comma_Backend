package com.bitharmony.comma.album.Image.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bitharmony.comma.album.file.dto.FileResponse;
import com.bitharmony.comma.album.file.service.FileService;
import com.bitharmony.comma.album.file.util.FileType;
import com.bitharmony.comma.album.file.util.NcpImageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumImageService {
	private final FileService fileService;
	private final NcpImageUtil ncpImageUtil;

	@Transactional
	public FileResponse uploadAlbumImage(MultipartFile musicImageFile) {
		return fileService.uploadFile(musicImageFile, ncpImageUtil.getBucketName());
	}

	public boolean checkImageFile(MultipartFile musicImageFile) {
		Optional<MultipartFile> imgFile = fileService.checkFileByType(musicImageFile, FileType.IMAGE);
		if (imgFile.isEmpty() || musicImageFile == null) return false;

		return true;
	}
}
