package org.accompany.backend.domain.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.notification.service.NotificationGenerator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationGenerator notificationGenerator;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void runDaily() {
        log.info("[NotificationScheduler] 일일 알림 배치 시작");
        notificationGenerator.generate();
        log.info("[NotificationScheduler] 일일 알림 배치 완료");
    }
}
