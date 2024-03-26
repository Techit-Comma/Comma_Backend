package com.bitharmony.comma.member.notification.controller;

import com.bitharmony.comma.global.exception.member.NoMoreNotificationException;
import com.bitharmony.comma.global.response.GlobalResponse;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.member.service.MemberService;
import com.bitharmony.comma.member.notification.dto.NotificationRequest;
import com.bitharmony.comma.member.notification.dto.NotificationResponse;
import com.bitharmony.comma.member.notification.repository.DeferredResultRepository;
import com.bitharmony.comma.member.notification.service.NotificationService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberService memberService;

    // 초기 요청 이후에 주기적인 Long Polling 요청을 통해서 새 알림이 발생하면 알림 목록 응답
    @GetMapping(value = "/subscribe")
    public DeferredResult<List<NotificationResponse>> getNewNotifications(Principal principal) {
        // 사용자 정보 가져오기
        Member member = memberService.getMemberByUsername(principal.getName());

        // DeferredResult 생성 및 저장소에 저장
        DeferredResult<List<NotificationResponse>> deferredResult =
                notificationService.getDeferredResult(member.getId().toString());

        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult(new NoMoreNotificationException());
        });

        return deferredResult;
    }

    // 처음 접근 시 전체 알림 받기 위한 요청
    @GetMapping
    public List<NotificationResponse> getNotifications(Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        return notificationService.getNotifications(member.getId().toString());
    }

    // 알림 읽음 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public void readNotification(@RequestBody NotificationRequest notificationRequest, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        notificationService.readNotification(notificationRequest.notificationId(), member);
    }

    // 알림 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public void removeNotification(@RequestBody NotificationRequest notificationRequest, Principal principal) {
        Member member = memberService.getMemberByUsername(principal.getName());
        notificationService.removeNotification(notificationRequest.notificationId(), member);
    }

    // 오래된 알림 삭제는 스케쥴러로 매 달마다 이루어지도록 설정
}
