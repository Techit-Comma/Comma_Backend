package com.bitharmony.comma.member.notification.service;

import com.bitharmony.comma.global.exception.member.NotAuthorizedException;
import com.bitharmony.comma.global.exception.member.NotificationNotFoundException;
import com.bitharmony.comma.global.util.Channel;
import com.bitharmony.comma.member.member.entity.Member;
import com.bitharmony.comma.member.follow.service.FollowService;
import com.bitharmony.comma.member.notification.dto.NotificationResponse;
import com.bitharmony.comma.member.notification.entity.Notification;
import com.bitharmony.comma.member.notification.repository.CompletableFutureRepository;
import com.bitharmony.comma.member.notification.repository.NotificationRepository;
import com.bitharmony.comma.member.notification.util.NotificationType;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    private final CompletableFutureRepository completableFutureRepository;
    private final FollowService followService;

    @Async
    @Transactional
    public void sendArtistNotification(Member artist, NotificationType notificationType, Long contentId) {
        String message = artist.getUsername() + notificationType.getMessage();

        // 아티스트를 팔로우하는 모든 팔로워 조회
        List<Member> followers = followService.getAllFollowerList(artist);

        for (Member follower : followers) {
            // 팔로워 별로 알림 생성
            Notification notification = notificationRepository.save(Notification.builder()
                    .message(message)
                    .redirectUrl(notificationType.getRedirectUrl() + contentId)
                    .publisher(artist)
                    .subscriber(follower)
                    .build()
            );

            // Redis Publish, 팔로워에게 메시지 전송 ex_) 1:1:아티스트님의 새 앨범이 등록되었습니다.
            redisTemplate.convertAndSend(Channel.ARTIST_NOTIFICATION.getName(), follower.getId()
                    + ":" + notification.getId()
                    + ":" + message
            );
        }
    }


    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Member member) {
        return notificationRepository.findAllBySubscriberIdOrderByCreateDateAsc(member.getId()).stream()
                .map(this::convertToNotificationResponse)
                .toList();
    }


    public CompletableFuture<String> getCompletableFuture(String key) {
        return completableFutureRepository.findByKey(key)
                .orElseGet(() -> {
                    CompletableFuture<String> completableFuture = new CompletableFuture<>();
                    completableFutureRepository.save(key, completableFuture);
                    return completableFuture;
                });
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


    @Transactional(readOnly = true)
    public Notification getNotification(Long notificationId) {
       return notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public void checkNotificationReceiver(Long receiverId, Long memberId) {
        if (!receiverId.equals(memberId)) {
            throw new NotAuthorizedException();
        }
    }

    private NotificationResponse convertToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .redirectUrl(notification.getRedirectUrl())
                .publisherName(notification.getPublisher().getNickname())
                .isRead(notification.getIsRead())
                .createDate(notification.getCreateDate())
                .build();
    }

}
