package com.bitharmony.comma.member.notification.service;

import com.bitharmony.comma.global.util.Channel;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.follow.service.FollowService;
import com.bitharmony.comma.member.notification.entity.Notification;
import com.bitharmony.comma.member.notification.repository.NotificationRepository;
import com.bitharmony.comma.member.notification.repository.SseEmitterRepository;
import com.bitharmony.comma.member.notification.repository.SseEmitterUtil;
import com.bitharmony.comma.member.notification.util.NotificationType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository sseEmitterRepository;
    private final FollowService followService;
    private final SseEmitterUtil sseEmitterUtil;


    @Async
    @Transactional
    public void sendArtistNotification(Member artist, NotificationType notificationType) {
        String message = artist.getUsername() + notificationType.getMessage();

        // 아티스트를 팔로우하는 모든 팔로워 조회
        List<Member> followers = followService.getAllFollowerList(artist);

        for (Member follower : followers) {
            // 팔로워 별로 알림 생성
            Notification notification = notificationRepository.save(Notification.builder()
                    .message(message)
                    .receiver(follower)
                    .build()
            );

            // Redis Publish, 팔로워에게 메시지 전송 ex_) 1:1:아티스트님의 새 앨범이 등록되었습니다.
            redisTemplate.convertAndSend(Channel.ARTIST_NOTIFICATION.getName(), follower.getId()
                    + ":" + notification.getId()
                    + ":" + message
            );
        }
    }


    @Transactional
    public SseEmitter subscribe(Member member, String lastEventId) { // TODO: 아티스트 별 SSEEmitter 생성 후 제작 작업 필요
        SseEmitter emitter = sseEmitterUtil.createSseEmitter();
        String key = member.getId().toString();

        emitter.onCompletion(() -> {
            sseEmitterUtil.complete(emitter, key);
        });

        emitter.onTimeout(emitter::complete);

        sseEmitterRepository.save(key, emitter);

        if (!lastEventId.isEmpty()) {
            // MySQL에서 해당 userId에 대한 미수신 이벤트 조회
            List<Notification> missedNotifications =
                    notificationRepository.findAllByReceiverIdAndIdGreaterThanOrderByCreateDateAsc(
                            member.getId(),
                            Long.parseLong(lastEventId)
                    );

            // 미수신 된 이벤트가 있다면 클라이언트로 전송
            missedNotifications.forEach(notification -> sseEmitterUtil.sendEventWithSseEmitter(
                    emitter, notification.getId().toString(), notification.getMessage())
            );
        }

        return emitter;
    }

}
