package org.accompany.backend.domain.notification.service;

public interface FcmSendService {

    FcmSendResult send(String fcmToken, FcmMessagePayload payload);
}
