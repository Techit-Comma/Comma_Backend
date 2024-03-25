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

    public List<String> getArticleImageByArticleId(long articleId) {
        List<ArticleImage> articleImages = articleImageRepository.findByArticleId(articleId);
        List<String> imageUrls = new ArrayList<>();
        for(ArticleImage image : articleImages){
            imageUrls.add(image.getImageUrl());
        }

        return imageUrls;
    }

    public ArticleImage getArticleImageById(long imageId) {
        return articleImageRepository.findById(imageId)
                .orElseThrow(ImageNotFoundException::new);
    }

    public ArticleImage getArticleImageByImageUrl(String imageUrl){
        return articleImageRepository.findByImageUrl(imageUrl).orElseThrow(ImageNotFoundException::new);
    }

    public String getImageUrl(String fileName){
        return ncpArticleImageUtil.getArticleCdn() + "/" + fileName + ncpArticleImageUtil.getImageCdnQueryString();
    }
}
