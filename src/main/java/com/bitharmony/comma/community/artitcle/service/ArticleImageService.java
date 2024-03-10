package com.bitharmony.comma.community.artitcle.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bitharmony.comma.community.artitcle.entity.Article;
import com.bitharmony.comma.community.artitcle.entity.ArticleImage;
import com.bitharmony.comma.community.artitcle.repository.ArticleImageRepository;
import com.bitharmony.comma.community.artitcle.util.NcpArticleImageUtil;
import com.bitharmony.comma.global.exception.community.DeleteArticleImageFailureException;
import com.bitharmony.comma.global.exception.community.ImageNotFoundException;
import com.bitharmony.comma.member.exception.DeleteOldProfileFailureException;
import com.bitharmony.comma.member.exception.UploadFailureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ArticleImageService {
    private final NcpArticleImageUtil ncpArticleImageUtil;
    private final ArticleImageRepository articleImageRepository;

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

        System.out.println(fileName);

        try {
            ncpArticleImageUtil.getAmazonS3().deleteObject(
                    new DeleteObjectRequest(ncpArticleImageUtil.getBucketName(), fileName)
            );
            System.out.println("삭제완료");
        } catch (Exception e) {
            throw new DeleteArticleImageFailureException();
        }
    }

    public void saveImageUrl(Article article, String imageUrl) {
        articleImageRepository.save(
                ArticleImage.builder()
                .article(article)
                .imageUrl(imageUrl)
                .build()
        );
    }

    public void deleteArticleImage(Long imageId) {
        ArticleImage articleImage = articleImageRepository.findById(imageId)
                .orElseThrow(ImageNotFoundException::new);
        articleImageRepository.delete(articleImage);
    }

    public Map<Long, String> getArticleImageByArticleId(long articleId) {
        List<ArticleImage> articleImages = articleImageRepository.findByArticleId(articleId);
        Map<Long, String> imageUrls = new HashMap<>();
        for(ArticleImage image : articleImages){
            imageUrls.put(image.getId(), image.getImageUrl());
        }

        return imageUrls;
    }

    public ArticleImage getArticleImageById(long imageId) {
        return articleImageRepository.findById(imageId)
                .orElseThrow(ImageNotFoundException::new);
    }
}