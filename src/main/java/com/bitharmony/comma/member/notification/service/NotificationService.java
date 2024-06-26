package com.bitharmony.comma.member.notification.service;

import com.bitharmony.comma.global.exception.member.NotAuthorizedException;
import com.bitharmony.comma.global.exception.member.NotificationNotFoundException;
import com.bitharmony.comma.global.util.Channel;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.follow.service.FollowService;
import com.bitharmony.comma.member.notification.dto.NotificationResponse;
import com.bitharmony.comma.member.notification.entity.Notification;
import com.bitharmony.comma.member.notification.repository.DeferredResultRepository;
import com.bitharmony.comma.member.notification.repository.NotificationRepository;
import com.bitharmony.comma.member.notification.util.NotificationConvertUtil;
import com.bitharmony.comma.member.notification.util.NotificationType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationRepository notificationRepository;
    private final DeferredResultRepository deferredResultRepository;
    private final NotificationConvertUtil notificationConvertUtil;
    private final FollowService followService;

    private final static Long TIMEOUT = 60000L;

    @Transactional
    public void sendArtistNotification(Member artist, NotificationType notificationType, Long contentId) {
        String message = artist.getUsername() + notificationType.getMessage();

        // 아티스트를 팔로우하는 모든 팔로워 조회
        List<Member> followers = followService.getAllFollowerList(artist);

        for (Member follower : followers) {
            String redirectUrl = notificationType.getRedirectUrl() + contentId;
            if (notificationType.equals(NotificationType.NEW_ARTICLE)) {
                redirectUrl = "/" + artist.getUsername() + notificationType.getRedirectUrl();
            }

            // 팔로워 별로 알림 생성
            Notification notification = notificationRepository.save(Notification.builder()
                    .message(message)
                    .redirectUrl(redirectUrl)
                    .publisher(artist)
                    .subscriber(follower)
                    .build()
            );

            // Redis Publish, 팔로워에게 메시지 전송 ex_) 2:1:아티스트님의 새 앨범이 등록되었습니다.
            redisTemplate.convertAndSend(Channel.ARTIST_NOTIFICATION.getName(), follower.getId()
                    + ":" + notification.getId()
                    + ":" + message
            );
        }
    }

    public DeferredResult<List<NotificationResponse>> getDeferredResult(String key) {
        return deferredResultRepository.findByKey(key)
                .orElseGet(() -> {
                    DeferredResult<List<NotificationResponse>> deferredResult = new DeferredResult<>(TIMEOUT);
                    deferredResultRepository.save(key, deferredResult);
                    return deferredResult;
                });
    }

    public List<NotificationResponse> getNotifications(String subscriberId) {
        return notificationRepository.findAllBySubscriberIdOrderByCreateDateDesc(Long.parseLong(subscriberId)).stream()
                .map(notificationConvertUtil::convertToNotificationResponse)
                .toList();
    }


    @Transactional
    public void readNotification(Long notificationId, Member member) {
        Notification notification = getNotification(notificationId);
        checkNotificationReceiver(notification.getSubscriber().getId(), member.getId());

        Notification _notification = notification.toBuilder()
                .isRead(true)
                .build();

        notificationRepository.save(_notification);
    }

    @Transactional
    public void removeNotification(Long notificationId, Member member) {
        Notification notification = getNotification(notificationId);
        checkNotificationReceiver(notification.getSubscriber().getId(), member.getId());

        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void removeOldNotification() {
        LocalDateTime limitDateTime = LocalDateTime.now().minusWeeks(2);
        notificationRepository.deleteAllByCreateDateBefore(limitDateTime);
    }


    @Transactional(readOnly = true)
    public Notification getNotification(Long notificationId) {
       return notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
    }

    public void checkNotificationReceiver(Long receiverId, Long memberId) {
        if (!receiverId.equals(memberId)) {
            throw new NotAuthorizedException();
        }
    }

}
