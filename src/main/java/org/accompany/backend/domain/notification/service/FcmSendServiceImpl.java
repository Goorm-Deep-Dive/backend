package org.accompany.backend.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmSendServiceImpl implements FcmSendService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BACKOFF_MS = 500L;

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public FcmSendResult send(String fcmToken, FcmMessagePayload payload) {
        log.info("[FCM] 발송 시작 - notificationId={}", payload.notificationId());

        Notification notification = Notification.builder()
                .setTitle(payload.deceasedName() + "님")
                .setBody(payload.body())
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putAllData(Map.of(
                        "notificationId", String.valueOf(payload.notificationId())
                ))
                .build();

        FirebaseMessagingException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String messageId = firebaseMessaging.send(message);
                log.info("[FCM] 발송 성공 - notificationId={}, messageId={}, attempt={}",
                        payload.notificationId(), messageId, attempt);
                return FcmSendResult.success(messageId);

            } catch (FirebaseMessagingException e) {
                lastException = e;
                log.warn("[FCM] 발송 실패 (시도 {}/{}) - notificationId={}, errorCode={}, reason={}",
                        attempt, MAX_RETRIES, payload.notificationId(),
                        e.getMessagingErrorCode(), e.getMessage());

                if (!isRetryable(e.getMessagingErrorCode()) || attempt == MAX_RETRIES) {
                    break;
                }

                try {
                    Thread.sleep(RETRY_BACKOFF_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.error("[FCM] 발송 최종 실패 - notificationId={}, errorCode={}",
                payload.notificationId(), lastException.getMessagingErrorCode());
        return FcmSendResult.failure("FCM_SEND_FAILED:" + lastException.getMessagingErrorCode());
    }

    private boolean isRetryable(MessagingErrorCode errorCode) {
        if (errorCode == null) return false;
        return switch (errorCode) {
            case UNREGISTERED, INVALID_ARGUMENT -> false;
            default -> true;
        };
    }
}
