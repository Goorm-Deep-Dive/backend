package org.accompany.backend.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import org.accompany.backend.domain.user.dto.response.UserProfileRes;

public interface UserService {
    UserProfileRes getMyProfile(Long userId);
    void updateNotification(Long userId, boolean notificationEnabled);
    void linkGoogleAccount(Long userId, String googleProviderUserId, String googleAccessToken, String googleRefreshToken);
    void withdraw(Long userId, HttpServletResponse response);
}
