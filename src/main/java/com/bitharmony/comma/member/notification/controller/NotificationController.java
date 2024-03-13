package com.bitharmony.comma.member.notification.controller;

import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.service.MemberService;
import com.bitharmony.comma.member.notification.dto.NotificationRequest;
import com.bitharmony.comma.member.notification.service.NotificationService;
import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    @GetMapping(value = "/subscribe")
    public CompletableFuture<GlobalResponse<?>> getNotification(Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        CompletableFuture<String> completableFuture = notificationService.getCompletableFuture(
                member.getId().toString()
        );

        return completableFuture.thenApply(message ->
                GlobalResponse.of("200", notificationService.getNotifications(member))
        );
    }

    // 알림 읽음 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public void readNotification(NotificationRequest notificationRequest, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        notificationService.readNotification(notificationRequest.id(), member);
    }

    // 알림 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public void removeNotification(NotificationRequest notificationRequest, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        notificationService.removeNotification(notificationRequest.id(), member);
    }

    // 오래된 알림 삭제는 스케쥴러로 매 달마다 이루어지도록 설정
}
