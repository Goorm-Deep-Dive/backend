package org.accompany.backend.domain.notification.repository;

import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.entity.NotificationDeliveryStatus;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
           "JOIN FETCH n.deceasedProfile " +
           "JOIN FETCH n.userProcedureChecklist " +
           "WHERE n.user = :user " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findAllByUserOrderByCreatedAtDesc(@Param("user") User user);

    long countByUserAndIsReadFalse(User user);

    @Query("SELECT ce FROM CalendarEvent ce " +
           "JOIN FETCH ce.userProcedureChecklist upc " +
           "JOIN FETCH upc.deceasedProfile dp " +
           "JOIN FETCH dp.user u " +
           "JOIN FETCH upc.procedure p " +
           "WHERE upc IS NOT NULL " +
           "AND u.isNotificationEnabled = true " +
           "AND upc.isChecked = false " +
           "AND ce.startAt >= :todayStart")
    List<CalendarEvent> findNotificationTargets(
            @Param("todayStart") LocalDateTime todayStart);

    @Query("SELECT ce FROM CalendarEvent ce " +
           "JOIN FETCH ce.userProcedureChecklist upc " +
           "JOIN FETCH upc.deceasedProfile dp " +
           "JOIN FETCH dp.user u " +
           "JOIN FETCH upc.procedure p " +
           "WHERE u.userId = :userId " +
           "AND upc IS NOT NULL " +
           "AND upc.isChecked = false " +
           "AND ce.startAt >= :todayStart")
    List<CalendarEvent> findNotificationTargetsByUser(
            @Param("userId") Long userId,
            @Param("todayStart") LocalDateTime todayStart);

    @Query("SELECT n FROM Notification n " +
           "JOIN FETCH n.deceasedProfile " +
           "WHERE n.idempotencyKey IN :keys " +
           "AND n.deliveryStatus = :status")
    List<Notification> findByIdempotencyKeysAndStatus(
            @Param("keys") List<String> keys,
            @Param("status") NotificationDeliveryStatus status);
}
