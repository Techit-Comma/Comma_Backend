package com.bitharmony.comma.community.artitcle.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.bitharmony.comma.community.artitcle.entity.Article;
import com.bitharmony.comma.community.artitcle.entity.ArticleImage;
import com.bitharmony.comma.community.artitcle.repository.ArticleImageRepository;
import com.bitharmony.comma.community.artitcle.util.NcpArticleImageUtil;
import com.bitharmony.comma.file.dto.FileResponse;
import com.bitharmony.comma.file.service.FileService;
import com.bitharmony.comma.global.exception.community.DeleteFileFailureException;
import com.bitharmony.comma.global.exception.community.ImageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ArticleImageService {
    private final NcpArticleImageUtil ncpArticleImageUtil;
    private final ArticleImageRepository articleImageRepository;
    private final FileService fileService;
    private final AmazonS3 amazonS3;

    public String uploadArticleImage(MultipartFile multipartFile) {
        FileResponse fileResponse = fileService.getFileResponse(
                multipartFile,
                ncpArticleImageUtil.getBucketName(),
                ncpArticleImageUtil.articleCdn,
                ncpArticleImageUtil.imageCdnQueryString
        );
        fileService.uploadFile(multipartFile,
                ncpArticleImageUtil.bucketName,
                fileResponse.uploadFileName());

        return fileResponse.uploadFileUrl();
    }


    public void deleteFile(String imagePath) {
        String fileName = fileService.replaceImagePath(imagePath,
                ncpArticleImageUtil.getArticleCdn(),
                ncpArticleImageUtil.getImageCdnQueryString()
        );

        fileService.deleteFile(fileName, ncpArticleImageUtil.getBucketName());
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
