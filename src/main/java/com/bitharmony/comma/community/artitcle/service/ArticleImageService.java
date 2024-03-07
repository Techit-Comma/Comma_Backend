package com.bitharmony.comma.community.artitcle.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bitharmony.comma.community.artitcle.util.NcpArticleImageUtil;
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
public class ArticleImageService {
    private final NcpArticleImageUtil ncpArticleImageUtil;

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID() + "." + ext;
    }

    public String uploadFile(MultipartFile multipartFile) {
        String uploadFileName = getUuidFileName(multipartFile.getOriginalFilename());
        String imageUrl = "";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            // S3에 폴더 및 파일 업로드
            ncpArticleImageUtil.getAmazonS3()
                    .putObject(
                            new PutObjectRequest(
                                    ncpArticleImageUtil.getBucketName(),
                                    uploadFileName,
                                    inputStream,
                                    objectMetadata
                            )
                                    .withCannedAcl(CannedAccessControlList.PublicRead)
                    );

            // 프로필 이미지 불러오는 Url
            imageUrl = ncpArticleImageUtil.getArticleCdn() + "/" + uploadFileName + ncpArticleImageUtil.getImageCdnQueryString();

        } catch (IOException e) {
            throw new UploadFailureException();
        }

        return imageUrl;
    }


    public void deleteFile(String imagePath) {
        String fileName = imagePath
                .replace(ncpArticleImageUtil.getArticleCdn(), "")
                .substring(1)
                .replace(ncpArticleImageUtil.getImageCdnQueryString(), "");

        try {
            ncpArticleImageUtil.getAmazonS3().deleteObject(
                    new DeleteObjectRequest(ncpArticleImageUtil.getBucketName(), fileName)
            );
            System.out.println("삭제완료");
        } catch (Exception e) {
            throw new DeleteFailureException();
        }
    }
}
