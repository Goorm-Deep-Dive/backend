package org.accompany.backend.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.notification.entity.Notification;
import org.accompany.backend.domain.notification.repository.NotificationBulkRepository;
import org.accompany.backend.domain.notification.repository.NotificationRepository;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationUserProcessor {

    private final NotificationRepository notificationRepository;
    private final NotificationBulkRepository notificationBulkRepository;

    @Transactional
    public void process(User user, List<UserProcedureChecklist> notificationTargetChecklists, LocalDate today) {
        log.info("[NotificationUserProcessor] 시작 - userId={}", user.getUserId());

        Map<Long, List<UserProcedureChecklist>> byProfile = notificationTargetChecklists.stream()
                .collect(Collectors.groupingBy(c -> c.getDeceasedProfile().getDeceasedProfileId()));

        List<Notification> toInsert = new ArrayList<>();

        for (Map.Entry<Long, List<UserProcedureChecklist>> entry : byProfile.entrySet()) {
            Long profileId = entry.getKey();
            List<UserProcedureChecklist> profileChecklists = entry.getValue();

            String idempotencyKey = buildIdempotencyKey(profileId, today);

            Optional<UserProcedureChecklist> closestOpt = profileChecklists.stream()
                    .min(Comparator
                            .comparing(UserProcedureChecklist::getDueDate)
                            .thenComparing(c -> c.getProcedure().getProcedureId()));

            if (closestOpt.isEmpty()) continue;

            UserProcedureChecklist closest = closestOpt.get();
            long daysLeft = ChronoUnit.DAYS.between(today, closest.getDueDate().toLocalDate());

            String message = "가장 빠른 기한까지 D-" + daysLeft + "일 남았어요";

            Notification notification = Notification.builder()
                    .user(user)
                    .deceasedProfile(closest.getDeceasedProfile())
                    .userProcedureChecklist(closest)
                    .message(message)
                    .isRead(false)
                    .idempotencyKey(idempotencyKey)
                    .build();

            toInsert.add(notification);
        }

        if (!toInsert.isEmpty()) {
            notificationBulkRepository.bulkInsert(toInsert);
        }

        log.info("[NotificationUserProcessor] 완료 - userId={}, 생성={}",
                user.getUserId(), toInsert.size());
    }

    private String buildIdempotencyKey(Long profileId, LocalDate today) {
        return "DDAY-" + profileId + "-" + today.toString();
    }
}
