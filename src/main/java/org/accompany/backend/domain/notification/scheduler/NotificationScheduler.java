package org.accompany.backend.domain.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.notification.service.NotificationGenerator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NotificationGenerator notificationGenerator;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void runHourly() {
        int hour = LocalDateTime.now(KST).getHour();
        log.info("[NotificationScheduler] 매시간 알림 배치 시작 - hour={}", hour);
        notificationGenerator.generate(hour);
        log.info("[NotificationScheduler] 매시간 알림 배치 완료 - hour={}", hour);
    }
}
