package com.bitharmony.comma.file.service;

import com.bitharmony.comma.file.dto.FileResponse;
import com.bitharmony.comma.file.util.FileType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bitharmony.comma.global.exception.album.AlbumFileException;
import com.bitharmony.comma.album.album.util.NcpImageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final NcpImageUtil ncpImageUtil;

	public String getUuidFileName(String fileName) {
		String ext = fileName.substring(fileName.indexOf(".") + 1);
		return UUID.randomUUID() + "." + ext;
	}

	public boolean isFileUploadedToS3(String filePath) {
		AmazonS3 s3Client = ncpImageUtil.getAmazonS3();
		GetObjectMetadataRequest getMetadataRequest = new GetObjectMetadataRequest(ncpImageUtil.getBucketName(), getFileName(filePath, ncpImageUtil.getBucketName()));

		try {
			s3Client.getObjectMetadata(getMetadataRequest);
			return true;
		} catch (AmazonS3Exception e) {
			if (e.getStatusCode() == 404) {
				// Object not found
				return false;
			} else {
				// Some other error. Rethrow the exception.
				throw e;
			}
		}
	}

	/**
	 *NOTICE: filePath의 맨 앞에 /는 안붙여도됨. ex) history/images
	 *ncp object storage에 파일 업로드
	 */
	public FileResponse uploadFile(MultipartFile multipartFile, String bucketName) {
		String originalFileName = multipartFile.getOriginalFilename();
		String uploadFileName = getUuidFileName(originalFileName);
		String uploadFileUrl;

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(multipartFile.getSize());
		objectMetadata.setContentType(multipartFile.getContentType());

		try (InputStream inputStream = multipartFile.getInputStream()) {
			String keyName = uploadFileName;

			// S3에 폴더 및 파일 업로드
			ncpImageUtil.getAmazonS3().putObject(
					new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata).withCannedAcl(
							CannedAccessControlList.PublicRead));

			// S3에 업로드한 폴더 및 파일 URL
			uploadFileUrl = bucketName + "/" + keyName;

		} catch (IOException e) {
			throw new AlbumFileException();
		}

		return FileResponse.builder()
				.originalFileName(originalFileName)
				.uploadFileName(uploadFileName)
				.uploadFileUrl(uploadFileUrl)
				.build();
	}

	public void deleteFile(String filePath, String bucketName) {
		if(filePath == null) return;
		ncpImageUtil.getAmazonS3().deleteObject(new DeleteObjectRequest(bucketName, getFileName(filePath, bucketName)));
	}

	public Optional<MultipartFile> checkFileByType(MultipartFile multipartFile, FileType fileType) {
		if (multipartFile != null && multipartFile.getContentType().startsWith(fileType.getType())) {
			return Optional.of(multipartFile);
		}
		return Optional.empty();
	}

	public String getFileName(String filepath, String bucketName) {
		String replacedFilePath = filepath.replace(bucketName, "");
		return replacedFilePath.substring(replacedFilePath.lastIndexOf("/") + 1);
	}
}