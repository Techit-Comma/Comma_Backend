package com.bitharmony.comma.member.member.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bitharmony.comma.member.member.dto.MemberImageResponse;
import com.bitharmony.comma.global.exception.member.DeleteOldProfileFailureException;
import com.bitharmony.comma.global.exception.member.UploadFailureException;
import com.bitharmony.comma.member.member.util.NcpProfileImageUtil;
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
    public final static String defaultProfileUrl = "https://kv6d2rdb2209.edge.naverncp.com/F82rLGPicA/default_profile.jpg?type=f&w=300&h=300&ttype=jpg";

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID() + "." + ext;
    }

    public MemberImageResponse uploadFile(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getUuidFileName(originalFileName);
        String imageUrl;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            String keyName = uploadFileName;

            // S3에 폴더 및 파일 업로드
            ncpProfileImageUtil.getAmazonS3().putObject(
                    new PutObjectRequest(ncpProfileImageUtil.getBucketName(), keyName, inputStream, objectMetadata).withCannedAcl(
                            CannedAccessControlList.PublicRead));

            // 프로필 이미지 불러오는 Url
            imageUrl = ncpProfileImageUtil.getMemberCdn() + "/" + keyName + ncpProfileImageUtil.getImageCdnQueryString();

        } catch (IOException e) {
            throw new UploadFailureException();
        }

        return MemberImageResponse.builder()
                .uploadFileName(uploadFileName)
                .profileImageUrl(imageUrl)
                .build();
    }


    public void deleteFile(String imagePath) {
        String fileName = imagePath
                .replace(ncpProfileImageUtil.getMemberCdn(), "")
                .substring(1)
                .replace(ncpProfileImageUtil.getImageCdnQueryString(), "");

        try {
            ncpProfileImageUtil.getAmazonS3().deleteObject(
                    new DeleteObjectRequest(ncpProfileImageUtil.getBucketName(), fileName)
            );
            System.out.println("삭제완료");
        } catch (Exception e) {
            throw new DeleteOldProfileFailureException();
        }
    }
}