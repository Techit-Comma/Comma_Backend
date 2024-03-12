package com.bitharmony.comma.album.file.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class NcpImageUtil {
	private final AmazonS3 amazonS3;
	private final NcpConfig ncpConfig;

	@Value("${ncp.s3.image-bucket}")
	private String bucketName;
	@Value("${ncp.image-optimizer.album-cdn}")
	private String imageCdn;
	@Value("${ncp.image-optimizer.member-cdn}")
	private String memberCdn;
	@Value("${ncp.image-optimizer.query-string}")
	private String imageCdnQueryString;

	public String getAlbumImageUrl(String filepath) {
		if (filepath == null) {
			return null;
		}

		return imageCdn + replaceBucketName(filepath, bucketName)
				+ imageCdnQueryString;
	}

	public String getAlbumFileUrl(String filepath) {
		return ncpConfig.endPoint + "/" + filepath;
	}

	private String replaceBucketName(String filepath, String bucketName) {
		return filepath.replace(bucketName, "");
	}
}