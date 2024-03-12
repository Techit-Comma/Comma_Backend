package com.bitharmony.comma.album.file.util;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.bitharmony.comma.global.config.NcpConfig;

import lombok.Getter;

@Getter
@Component
public class NcpImageUtil {
	private final AmazonS3 amazonS3;
	private final String bucketName;
	private final String endPoint;
	private final String imageCdn;
	private final String memberCdn;
	private final String imageCdnQueryString;

	public NcpImageUtil(NcpConfig ncpConfig) {
		bucketName = ncpConfig.getS3().getImageBucket();
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

	public String getAlbumImageUrl(String filepath) {
		if (filepath == null) {
			return null;
		}

		return imageCdn + replaceBucketName(filepath, bucketName)
				+ imageCdnQueryString;
	}

	public String getAlbumFileUrl(String filepath) { // 미사용 메서드이긴 하나, 향후 추가 리팩토링 시 사용여부 결정
		return endPoint + "/" + replaceBucketName(filepath, bucketName);
	}

	private String replaceBucketName(String filepath, String bucketName) {
		return filepath.replace(bucketName, "");
	}
}