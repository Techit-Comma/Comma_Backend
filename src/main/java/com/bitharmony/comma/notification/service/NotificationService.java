package com.bitharmony.comma.notification.service;

import com.bitharmony.comma.member.entity.Member;
import com.bitharmony.comma.member.follow.dto.FollowingListResponse;
import com.bitharmony.comma.member.follow.service.FollowService;
import com.bitharmony.comma.notification.entity.Notification;
import com.bitharmony.comma.notification.repository.NotificationRepository;
import com.bitharmony.comma.notification.repository.SseEmitterRepository;
import com.bitharmony.comma.notification.util.ArtistNotificationListener;
import com.bitharmony.comma.notification.util.NotificationType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisMessageListenerContainer container;
    private final ArtistNotificationListener artistNotificationListener;
    private final ChannelTopic topicNotification;
    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository sseEmitterRepository;
    private final FollowService followService;

    private final static String CHANNEL_NAME = "artistNotification";
    private final static Long TIMEOUT = 60000L * 10;

    public void sendArtistNotification(Member artist, NotificationType notificationType) {
        String message = artist.getUsername() + notificationType.getMessage();

        // MySQL 저장을 위한 알림 생성
        Notification notification = Notification.builder()
                .message(message)
                .receiver(artist)
                .build();

        notificationRepository.save(notification);

        // Redis Publish, 구독자들에게 메시지 전송
        redisTemplate.convertAndSend(CHANNEL_NAME, message);
    }

    public SseEmitter subscribe(Member member, String lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        String key = member.getId().toString();

        emitter.onCompletion(() -> {
            sseEmitterRepository.deleteByKey(key);
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
            missedNotifications.forEach(notification ->
                    sendToClient(emitter, notification.getId().toString(), notification.getMessage()));
        }

        return emitter;
    }

    @Transactional
    public void addNotification(NotificationType notificationType, Member caller) {
        List<Member> followers = followService.getAllFollowerList(caller);

        for(Member follower: followers) {
            Notification notification = Notification.builder()
                    .message(caller.getNickname() + notificationType.getMessage())
                    .receiver(follower)
                    .build();

            notificationRepository.save(notification);
        }
    }

/*    @Transactional(readOnly = true)
    public List<Notification> getNotification() {
        List<Long> memberIds = followService.getAllFollowingList().stream()
                .map(FollowingListResponse::memberId).toList();

        return notificationRepository.findAllByCallerIdInOrderByCreateDateDesc(memberIds);
    }*/

}
