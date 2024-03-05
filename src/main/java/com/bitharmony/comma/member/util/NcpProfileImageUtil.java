package com.bitharmony.comma.member.util;

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
public class NcpProfileImageUtil {
    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String endPoint;
    private final String imageCdn;
    private final String memberCdn;
    private final String imageCdnQueryString;

    public NcpProfileImageUtil(NcpConfig ncpConfig) {
        bucketName = ncpConfig.getS3().getProfileImageBucket();
        endPoint = ncpConfig.getS3().getEndPoint();
        imageCdn = ncpConfig.getImageOptimizer().getAlbumCdn();
        memberCdn = ncpConfig.getImageOptimizer().getMemberCdn();
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
