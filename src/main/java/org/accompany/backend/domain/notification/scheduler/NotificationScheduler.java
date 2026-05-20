package org.accompany.backend.domain.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.notification.entity.NotificationSlot;
import org.accompany.backend.domain.notification.service.NotificationGenerator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationGenerator notificationGenerator;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void runMorning() {
        log.info("[NotificationScheduler] 오전 알림 배치 시작 - slot=AM");
        notificationGenerator.generate(NotificationSlot.AM);
        log.info("[NotificationScheduler] 오전 알림 배치 완료 - slot=AM");
    }

    @Scheduled(cron = "0 30 16 * * *", zone = "Asia/Seoul")
    public void runAfternoon() {
        log.info("[NotificationScheduler] 오후 알림 배치 시작 - slot=PM");
        notificationGenerator.generate(NotificationSlot.PM);
        log.info("[NotificationScheduler] 오후 알림 배치 완료 - slot=PM");
    }
}
