package com.bitharmony.comma.album.Image.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bitharmony.comma.file.dto.FileResponse;
import com.bitharmony.comma.file.service.FileService;
import com.bitharmony.comma.file.util.FileType;
import com.bitharmony.comma.album.album.util.NcpImageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumImageService {
	private final FileService fileService;
	private final NcpImageUtil ncpImageUtil;

	@Transactional
	public FileResponse uploadAlbumImage(MultipartFile musicImageFile) {
		FileResponse fileResponse = fileService.getFileResponse(musicImageFile,
				ncpImageUtil.getBucketName(),
				ncpImageUtil.getImageCdn(),
				ncpImageUtil.getImageCdnQueryString()
		);
		fileService.uploadFile(musicImageFile, ncpImageUtil.getBucketName(), fileResponse.uploadFileName());
		return fileResponse;
	}

	public boolean checkImageFile(MultipartFile musicImageFile) {
		Optional<MultipartFile> imgFile = fileService.checkFileByType(musicImageFile, FileType.IMAGE);
        return imgFile.isPresent() && musicImageFile != null;
    }
}
