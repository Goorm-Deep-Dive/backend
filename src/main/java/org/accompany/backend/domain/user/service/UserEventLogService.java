package org.accompany.backend.domain.user.service;

import org.accompany.backend.domain.user.entity.Provider;

public interface UserEventLogService {
    void recordLoginAttempt(String deviceId, Provider provider);
    void recordLoginSuccess(String deviceId, Long userId, Provider provider);
    void recordLoginFailure(String deviceId, Provider provider, String failureReason);
    void recordSignUp(Long userId, Provider provider);
    void recordLogout(Long userId);
    void recordWithdrawal(Long userId, Provider provider);
}
