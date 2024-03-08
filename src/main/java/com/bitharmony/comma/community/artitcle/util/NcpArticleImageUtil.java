package com.bitharmony.comma.community.artitcle.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.bitharmony.comma.global.config.NcpConfig;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NcpArticleImageUtil {
    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String endPoint;
    private final String articleCdn;
    private final String imageCdnQueryString;

    public NcpArticleImageUtil(NcpConfig ncpConfig) {
        bucketName = ncpConfig.getS3().getArticleImageBucket();
        endPoint = ncpConfig.getS3().getEndPoint();
        articleCdn = ncpConfig.getImageOptimizer().getArticleCdn();
        imageCdnQueryString = ncpConfig.getImageOptimizer().getQueryString();

        String accessKey = ncpConfig.getImageCredentials().getAccessKey();
        String secretKey = ncpConfig.getImageCredentials().getSecretKey();
        String endPoint = ncpConfig.getS3().getEndPoint();
        String region = ncpConfig.getS3().getRegion();

        amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }
}
