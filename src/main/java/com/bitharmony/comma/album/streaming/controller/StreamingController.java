package com.bitharmony.comma.album.streaming.controller;

import com.bitharmony.comma.album.streaming.dto.EncodeStatusRequest;
import com.bitharmony.comma.album.streaming.dto.UploadUrlRequest;
import com.bitharmony.comma.album.streaming.dto.UploadUrlResponse;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.album.streaming.service.StreamingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/upload") // get upload presigned url
    public GlobalResponse<UploadUrlResponse> getUploadURL(@ModelAttribute @Valid UploadUrlRequest uploadUrlRequest) {
        return GlobalResponse.of("200", streamingService.generateURL(uploadUrlRequest.filename()));
    }

    @Async
    @PostMapping("/status") // callback encode status
    public void encodeStatus(@RequestBody EncodeStatusRequest encodeStatusRequest) {
        streamingService.encodeStatus(encodeStatusRequest.filePath(),
                encodeStatusRequest.outputType(), encodeStatusRequest.status());
    }

    @GetMapping("/status") // sse emitter subscribe
    public SseEmitter getEncodeStatus(@RequestParam(name = "filePath") String filePath) {
        return streamingService.subscribe(streamingService.extractUUID(filePath));
    }

}
