package org.accompany.backend.domain.user.event;

import org.accompany.backend.domain.user.entity.Provider;
import org.accompany.backend.domain.user.entity.UserEventType;

public record UserEvent(
        UserEventType eventType,
        String deviceId,
        Provider provider,
        Long userId,
        String failureReason
) {
    public static UserEvent attempt(String deviceId, Provider provider) {
        return new UserEvent(UserEventType.LOGIN_ATTEMPT, deviceId, provider, null, null);
    }

    public static UserEvent success(String deviceId, Long userId, Provider provider) {
        return new UserEvent(UserEventType.LOGIN_SUCCESS, deviceId, provider, userId, null);
    }

    public static UserEvent failure(String deviceId, Provider provider, String failureReason) {
        return new UserEvent(UserEventType.LOGIN_FAILURE, deviceId, provider, null, failureReason);
    }

    public static UserEvent logout(Long userId) {
        return new UserEvent(UserEventType.LOGOUT, null, null, userId, null);
    }

    public static UserEvent signUp(Long userId, Provider provider) {
        return new UserEvent(UserEventType.SIGN_UP, null, provider, userId, null);
    }

    public static UserEvent withdrawal(Long userId, Provider provider) {
        return new UserEvent(UserEventType.WITHDRAWAL, null, provider, userId, null);
    }
}
