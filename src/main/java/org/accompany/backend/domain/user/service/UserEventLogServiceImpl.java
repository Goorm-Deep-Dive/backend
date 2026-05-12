package org.accompany.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.Provider;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.entity.UserEventLog;
import org.accompany.backend.domain.user.entity.UserEventType;
import org.accompany.backend.domain.user.repository.UserEventLogRepository;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserEventLogServiceImpl implements UserEventLogService {

    private final UserEventLogRepository userEventLogRepository;
    private final UserRepository userRepository;

    @Override
    public void recordLoginAttempt(String deviceId, Provider provider) {
        userEventLogRepository.save(UserEventLog.builder()
                .deviceId(deviceId)
                .provider(provider)
                .eventType(UserEventType.LOGIN_ATTEMPT)
                .build());

        log.info("[EventLog] 로그인 시도 - deviceId: {}, provider: {}", deviceId, provider);
    }

    @Override
    public void recordLoginSuccess(String deviceId, Long userId, Provider provider) {
        User user = userRepository.getReferenceById(userId);

        userEventLogRepository.save(UserEventLog.builder()
                .deviceId(deviceId)
                .user(user)
                .provider(provider)
                .eventType(UserEventType.LOGIN_SUCCESS)
                .build());

        log.info("[EventLog] 로그인 성공 - deviceId: {}, userId: {}", deviceId, user.getUserId());
    }

    @Override
    public void recordLoginFailure(String deviceId, Provider provider, String failureReason) {
        userEventLogRepository.save(UserEventLog.builder()
                .deviceId(deviceId)
                .provider(provider)
                .eventType(UserEventType.LOGIN_FAILURE)
                .failureReason(failureReason)
                .build());

        log.info("[EventLog] 로그인 실패 - deviceId: {}, provider: {}, reason: {}", deviceId, provider, failureReason);
    }

    @Override
    public void recordSignUp(Long userId, Provider provider) {
        User user = userRepository.getReferenceById(userId);

        userEventLogRepository.save(UserEventLog.builder()
                .user(user)
                .provider(provider)
                .eventType(UserEventType.SIGN_UP)
                .build());

        log.info("[EventLog] 회원가입 - userId: {}", userId);
    }

    @Override
    public void recordLogout(Long userId) {
        User user = userRepository.getReferenceById(userId);

        userEventLogRepository.save(UserEventLog.builder()
                .user(user)
                .eventType(UserEventType.LOGOUT)
                .build());

        log.info("[EventLog] 로그아웃 - userId: {}", userId);
    }

    @Override
    public void recordWithdrawal(Long userId, Provider provider) {
        userEventLogRepository.save(UserEventLog.builder()
                .provider(provider)
                .eventType(UserEventType.WITHDRAWAL)
                .build());

        log.info("[EventLog] 회원탈퇴 - userId: {}", userId);
    }
}
