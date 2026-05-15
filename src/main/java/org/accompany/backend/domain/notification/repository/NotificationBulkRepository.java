package org.accompany.backend.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.entity.NotificationDeliveryStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsert(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            return;
        }

        String sql = """
                insert into ending_schema.notifications (
                    user_id,
                    deceased_profile_id,
                    user_procedure_checklist_id,
                    message,
                    is_read,
                    idempotency_key,
                    delivery_status,
                    created_at,
                    updated_at
                ) values (?, ?, ?, ?, ?, ?, ?::ending_schema.notification_delivery_status, ?, ?)
                on conflict (idempotency_key) do nothing
                """;

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.batchUpdate(sql, notifications, notifications.size(),
                (ps, n) -> {
                    ps.setLong(1, n.getUser().getUserId());
                    ps.setLong(2, n.getDeceasedProfile().getDeceasedProfileId());
                    ps.setLong(3, n.getUserProcedureChecklist().getUserProcedureChecklistId());
                    ps.setString(4, n.getMessage());
                    ps.setBoolean(5, n.isRead());
                    ps.setString(6, n.getIdempotencyKey());
                    ps.setString(7, NotificationDeliveryStatus.PENDING.name());
                    ps.setTimestamp(8, Timestamp.valueOf(now));
                    ps.setTimestamp(9, Timestamp.valueOf(now));
                });
    }
}
