package com.bitharmony.comma.member.notification.repository;

import com.bitharmony.comma.member.notification.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllBySubscriberIdOrderByCreateDateAsc(Long userId);
    void deleteAllByCreateDateBefore(LocalDateTime limitDate);
}
