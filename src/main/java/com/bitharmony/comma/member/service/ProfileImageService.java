package com.bitharmony.comma.member.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bitharmony.comma.member.dto.MemberImageResponse;
import com.bitharmony.comma.member.exception.DeleteFailureException;
import com.bitharmony.comma.member.exception.UploadFailureException;
import com.bitharmony.comma.member.util.NcpProfileImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageService {
	private final NcpProfileImageUtil ncpProfileImageUtil;

	public String getUuidFileName(String fileName) {
		String ext = fileName.substring(fileName.indexOf(".") + 1);
		return UUID.randomUUID() + "." + ext;
	}

	public MemberImageResponse uploadFile(MultipartFile multipartFile) {
		String originalFileName = multipartFile.getOriginalFilename();
		String uploadFileName = getUuidFileName(originalFileName);
		String uploadFileUrl = "";

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(multipartFile.getSize());
		objectMetadata.setContentType(multipartFile.getContentType());

		try (InputStream inputStream = multipartFile.getInputStream()) {
			String keyName = uploadFileName;

			// S3에 폴더 및 파일 업로드
			ncpProfileImageUtil.getAmazonS3().putObject(
					new PutObjectRequest(ncpProfileImageUtil.getBucketName(), keyName, inputStream, objectMetadata).withCannedAcl(
							CannedAccessControlList.PublicRead));

			// S3에 업로드한 폴더 및 파일 URL
			uploadFileUrl = ncpProfileImageUtil.getBucketName() + "/" + keyName;

		} catch (IOException e) {
			throw new UploadFailureException();
		}

		return MemberImageResponse.builder()
				.originalFileName(originalFileName)
				.uploadFileName(uploadFileName)
				.uploadFileUrl(uploadFileUrl)
				.build();
	}


	public void deleteFile(String imagePath) {
		String bucketName = imagePath.split("/")[0];
		String fileName = imagePath.split("/")[1];

		try{
			ncpProfileImageUtil.getAmazonS3().deleteObject(
					new DeleteObjectRequest(bucketName,fileName)
			);
		} catch (Exception e){
			throw new DeleteFailureException();
		}
	}

	private String subtractBucketName(String filepath, String bucketName) {
		return filepath.replace(bucketName, "");
	}
	public String getProfileImageUrl(String imagePath) {
		if (imagePath == null) {
			return null;
		}

		return ncpProfileImageUtil.getMemberCdn() + subtractBucketName(imagePath, ncpProfileImageUtil.getBucketName())
				+ ncpProfileImageUtil.getImageCdnQueryString();
	}
}