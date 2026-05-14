package org.accompany.backend.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.accompany.backend.domain.user.entity.Role;
import org.accompany.backend.global.security.dto.TokenRes;

public interface UserAuthService {
    TokenRes refresh(HttpServletRequest request, HttpServletResponse response);
    void loginSuccess(Long userId, Role role, String providerAccessToken, String providerRefreshToken, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
}
