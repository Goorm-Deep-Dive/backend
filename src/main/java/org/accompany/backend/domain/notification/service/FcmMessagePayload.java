package org.accompany.backend.domain.notification.service;

public record FcmMessagePayload(
        String deceasedName,    // 푸시 제목으로 사용 (고인 이름)
        String body,            // 푸시 본문
        Long notificationId     // notifications 테이블의 PK
) {
}
