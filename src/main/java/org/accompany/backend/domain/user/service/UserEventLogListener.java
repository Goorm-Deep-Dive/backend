package org.accompany.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.user.event.UserEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventLogListener {

    private final UserEventLogService userEventLogService;

    @Async("eventLogExecutor")
    @EventListener
    public void onLoginEvent(UserEvent event) {
        switch (event.eventType()) {
            case LOGIN_ATTEMPT -> userEventLogService.recordLoginAttempt(event.deviceId(), event.provider());
            case LOGIN_SUCCESS -> userEventLogService.recordLoginSuccess(event.deviceId(), event.userId(), event.provider());
            case LOGIN_FAILURE -> userEventLogService.recordLoginFailure(event.deviceId(), event.provider(), event.failureReason());
            case SIGN_UP       -> userEventLogService.recordSignUp(event.userId(), event.provider());
            case LOGOUT        -> userEventLogService.recordLogout(event.userId());
            case WITHDRAWAL    -> userEventLogService.recordWithdrawal(event.userId(), event.provider());
        }
    }
}
