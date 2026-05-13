package org.accompany.backend.domain.notification.repository;

import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    long countByUserAndIsReadFalse(User user);
}
