package com.bitharmony.comma.file.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.bitharmony.comma.file.dto.FileResponse;
import com.bitharmony.comma.file.util.FileType;
import com.bitharmony.comma.global.exception.community.DeleteFileFailureException;
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
	private final AmazonS3 amazonS3;

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
	public void uploadFile(MultipartFile multipartFile, String bucketName, String uploadFileName) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(multipartFile.getSize());
		objectMetadata.setContentType(multipartFile.getContentType());

		try (InputStream inputStream = multipartFile.getInputStream()) {
            // S3에 폴더 및 파일 업로드
			amazonS3.putObject(
					new PutObjectRequest(bucketName, uploadFileName, inputStream, objectMetadata).withCannedAcl(
							CannedAccessControlList.PublicRead));

		} catch (IOException e) {
			throw new AlbumFileException();
		}
	}

	public FileResponse getFileResponse(MultipartFile multipartFile, String bucketName, String cdn, String queryString) {
		String originalFileName = multipartFile.getOriginalFilename();
		String uploadFileName = getUuidFileName(originalFileName);

		return FileResponse.builder()
				.originalFileName(originalFileName)
				.uploadFileName(uploadFileName)
				.uploadFilePath(bucketName + "/" + uploadFileName)
				.uploadFileUrl(cdn + "/" + uploadFileName + queryString)
				.build();
	}

	public String replaceImagePath(String imagePath, String cdn, String queryString) {
		return imagePath
				.replace(cdn, "")
				.substring(1)
				.replace(queryString, "");
	}


	public void deleteFile(String filename, String bucketName) {
		if(filename == null) throw new DeleteFileFailureException();

		try {
			amazonS3.deleteObject(new DeleteObjectRequest(bucketName, filename));
		} catch (SdkClientException e) {
			throw new DeleteFileFailureException();
		}
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