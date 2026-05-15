package org.accompany.backend.domain.notification.repository;

import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.procedure.entity.DueDateType;
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

    @Query("SELECT upc FROM UserProcedureChecklist upc " +
           "JOIN FETCH upc.deceasedProfile dp " +
           "JOIN FETCH dp.user u " +
           "JOIN FETCH upc.procedure p " +
           "WHERE u.isNotificationEnabled = true " +
           "AND upc.isChecked = false " +
           "AND upc.dueDate IS NOT NULL " +
           "AND upc.dueDate >= :todayStart " +
           "AND p.dueDateType NOT IN :excludedTypes")
    List<UserProcedureChecklist> findNotificationTargetChecklists(
            @Param("todayStart") LocalDateTime todayStart,
            @Param("excludedTypes") List<DueDateType> excludedTypes);
}
