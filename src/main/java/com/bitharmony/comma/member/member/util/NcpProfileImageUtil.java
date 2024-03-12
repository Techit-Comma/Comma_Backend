package com.bitharmony.comma.member.member.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.bitharmony.comma.global.config.NcpConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class NcpProfileImageUtil {
    private final AmazonS3 amazonS3;

    @Value("${ncp.s3.profile-image-bucket}")
    private String bucketName;

    @Value("${ncp.image-optimizer.member-cdn}")
    private String memberCdn;

    @Value("${ncp.image-optimizer.query-string}")
    private String imageCdnQueryString;
}
