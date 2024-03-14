package com.bitharmony.comma.album.streaming.service;

import com.bitharmony.comma.album.streaming.dto.UploadUrlResponse;
import com.bitharmony.comma.album.streaming.util.EncodeStatus;
import com.bitharmony.comma.album.streaming.util.NcpMusicUtil;
import com.bitharmony.comma.album.streaming.util.SseEmitterUtil;
import com.bitharmony.comma.global.exception.streaming.EncodingFailureException;
import com.bitharmony.comma.global.exception.streaming.EncodingStatusNotFoundException;
import com.bitharmony.comma.global.util.Channel;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SseEmitterUtil sseEmitterUtil;
    private final NcpMusicUtil ncpMusicUtil;


    public UploadUrlResponse generateURL(String fileName) {
        String filePath = ncpMusicUtil.getPath() + UUID.randomUUID() + getExtension(fileName);
        return UploadUrlResponse.from(ncpMusicUtil.generatePresignedUrl(filePath), filePath);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    public void encodeStatus(String filePath, String outputType, EncodeStatus status) {
        switch (status) {
            case WAITING -> sendEncodingStatus(filePath, outputType, EncodeStatus.WAITING);
            case RUNNING -> sendEncodingStatus(filePath, outputType, EncodeStatus.RUNNING);
            case COMPLETE -> sendEncodingStatus(filePath, outputType, EncodeStatus.COMPLETE);
            case FAILURE -> {
                sendEncodingStatus(filePath, outputType, EncodeStatus.FAILURE);
                throw new EncodingFailureException();
            }
            case CANCELED -> sendEncodingStatus(filePath, outputType, EncodeStatus.CANCELED);
        }
    }

    public SseEmitter subscribe(String filePath) {
        return sseEmitterUtil.generateSseEmitter(filePath);
    }

    private void sendEncodingStatus(String filePath, String outputType, EncodeStatus status) {
        String message = extractUUID(filePath) + ":" + outputType;
        redisTemplate.convertAndSend(Channel.ENCODING_STATUS.getName(), message + ":" + status);
    }

    public String extractUUID(String filePath) {
        String regex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filePath);
        if (matcher.find()) {
            return matcher.group();
        }

        throw new EncodingStatusNotFoundException();
    }

}
