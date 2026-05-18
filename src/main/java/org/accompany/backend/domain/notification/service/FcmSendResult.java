package org.accompany.backend.domain.notification.service;

public record FcmSendResult(
        boolean success,
        String messageId,      // 성공 시 FCM이 부여하는 메시지 ID
        String failureReason   // 실패 시 에러 메시지
) {
    public static FcmSendResult success(String messageId) {
        return new FcmSendResult(true, messageId, null);
    }

    public static FcmSendResult failure(String reason) {
        return new FcmSendResult(false, null, reason);
    }
}
