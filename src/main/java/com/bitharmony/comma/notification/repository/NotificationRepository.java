package com.bitharmony.comma.notification.repository;

import com.bitharmony.comma.notification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiverIdAndIdGreaterThanOrderByCreateDateAsc(Long userId, Long lastEventId);
}
