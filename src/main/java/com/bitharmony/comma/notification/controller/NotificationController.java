package com.bitharmony.comma.notification.controller;

import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.member.service.MemberService;
import com.bitharmony.comma.notification.service.NotificationService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter getNotification(Principal principal,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId
    ) {
        Member member = memberService.getMemberByUsername(principal.getName());
        return notificationService.subscribe(member, lastEventId);
    }

    // 알림 읽음 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public void readNotification() {

    }

    // 알림 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public void removeNotification() {

    }

    // 오래된 알림 삭제는 스케쥴러로 매 달마다 이루어지도록 설정
}
