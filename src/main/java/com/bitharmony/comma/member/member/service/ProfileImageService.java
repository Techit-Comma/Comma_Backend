package com.bitharmony.comma.member.member.service;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.bitharmony.comma.file.dto.FileResponse;
import com.bitharmony.comma.file.service.FileService;
import com.bitharmony.comma.member.member.dto.MemberImageResponse;
import com.bitharmony.comma.global.exception.member.DeleteOldProfileFailureException;
import com.bitharmony.comma.member.member.util.NcpProfileImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageService {
    private final FileService fileService;
    private final NcpProfileImageUtil ncpProfileImageUtil;
    public final static String defaultProfileUrl = "https://kv6d2rdb2209.edge.naverncp.com/F82rLGPicA/default_profile.jpg?type=f&w=300&h=300&ttype=jpg";

    public MemberImageResponse uploadMemberImage(MultipartFile multipartFile) {
        FileResponse fileResponse = fileService.getFileResponse(multipartFile,
                ncpProfileImageUtil.getBucketName(),
                ncpProfileImageUtil.getMemberCdn(),
                ncpProfileImageUtil.getImageCdnQueryString()
        );
        fileService.uploadFile(multipartFile, ncpProfileImageUtil.getBucketName(), fileResponse.uploadFileName());

        return MemberImageResponse.builder()
                .uploadFileName(fileResponse.uploadFileName())
                .profileImageUrl(fileResponse.uploadFileUrl())
                .build();
    }


    public void deleteFile(String imagePath) {
        String fileName = fileService.replaceImagePath(imagePath,
                ncpProfileImageUtil.getMemberCdn(),
                ncpProfileImageUtil.getImageCdnQueryString()
        );

        fileService.deleteFile(fileName, ncpProfileImageUtil.getBucketName());
    }
}