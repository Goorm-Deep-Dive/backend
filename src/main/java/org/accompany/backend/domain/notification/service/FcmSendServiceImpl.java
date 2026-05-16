package org.accompany.backend.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmSendServiceImpl implements FcmSendService {

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

        try {
            String messageId = firebaseMessaging.send(message);
            log.info("[FCM] 발송 성공 - notificationId={}, messageId={}",
                    payload.notificationId(), messageId);
            return FcmSendResult.success(messageId);

        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 발송 실패 - notificationId={}, errorCode={}, reason={}",
                    payload.notificationId(), e.getErrorCode(), e.getMessage());
            return FcmSendResult.failure("FCM_SEND_FAILED:" + e.getErrorCode());
        }
    }
}
