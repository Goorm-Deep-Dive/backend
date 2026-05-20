package org.accompany.backend.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.notification.entity.NotificationSlot;
import org.accompany.backend.domain.notification.repository.NotificationRepository;
import org.accompany.backend.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationGenerator {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NotificationRepository notificationRepository;
    private final NotificationUserProcessor userProcessor;

    public void generate(NotificationSlot slot) {
        log.info("[NotificationGenerator] 전체 알림 생성 시작 - slot={}", slot);

        LocalDate today = LocalDate.now(KST);
        LocalDateTime todayStart = today.atStartOfDay();

        List<CalendarEvent> notificationTargets = notificationRepository
                .findNotificationTargets(todayStart);

        Map<User, List<CalendarEvent>> byUser = notificationTargets.stream()
                .collect(Collectors.groupingBy(ce -> ce.getUserProcedureChecklist().getDeceasedProfile().getUser()));

        log.info("[NotificationGenerator] 대상 사용자 수={}, 후보 캘린더 이벤트 수={}",
                byUser.size(), notificationTargets.size());

        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<User, List<CalendarEvent>> entry : byUser.entrySet()) {
            try {
                userProcessor.process(entry.getKey(), entry.getValue(), today, slot);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("[NotificationGenerator] 사용자 알림 생성 실패 - userId={}",
                        entry.getKey().getUserId(), e);
            }
        }

        log.info("[NotificationGenerator] 전체 알림 생성 완료 - slot={}, 성공={}, 실패={}",
                slot, successCount, failCount);
    }
}
