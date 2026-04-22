package org.accompany.backend.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.RefreshToken;
import org.accompany.backend.domain.user.entity.Role;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.RefreshTokenRepository;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.security.dto.TokenRes;
import org.accompany.backend.global.security.jwt.JwtCookieProvider;
import org.accompany.backend.global.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAuthServiceImpl implements UserAuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieProvider jwtCookieProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    /**
     * RefreshToken 검증 후 AccessToken 재발급
     */
    @Override
    @Transactional
    public TokenRes refresh(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtCookieProvider.resolveRefreshToken(request);

        if (token == null) {
            log.warn("[Auth] RefreshToken 없음 - 재발급 요청 실패");
            throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
        }

        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("[Auth] 유효하지 않은 RefreshToken - 재발급 요청 실패");
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> {
                    log.warn("[Auth] 저장된 RefreshToken 조회 실패");
                    return new IllegalArgumentException("저장된 리프레시 토큰이 없습니다.");
                });

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("[Auth] 만료된 RefreshToken - userId: {}", refreshToken.getUser().getUserId());
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
        }

        try {
            User user = refreshToken.getUser();

            String newAccessToken = jwtTokenProvider.createAccessToken(
                    user.getUserId(),
                    user.getRole()
            );

            log.info("[Auth] AccessToken 재발급 완료 - userId: {}", user.getUserId());

            return new TokenRes(newAccessToken);

        } catch (Exception e) {
            log.error("[Auth] AccessToken 재발급 중 서버 오류 발생", e);
            throw e;
        }
    }

    /**
     * OAuth2 로그인 성공 후 AccessToken, RefreshToken 발급 및 저장
     */
    @Override
    @Transactional
    public void loginSuccess(Long userId,
                                 Role role,
                                 String providerAccessToken,
                                 String providerRefreshToken,
                                 HttpServletResponse response) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[Auth] 로그인 성공 처리 실패 - 사용자 조회 실패, userId: {}", userId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        try {

            user.updateSocialToken(providerAccessToken, providerRefreshToken);

            String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getUserId());

            LocalDateTime expiresAt = jwtTokenProvider.getExpiration(refreshTokenValue)
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            refreshTokenRepository.deleteByUser(user);

            RefreshToken refreshToken = RefreshToken.builder()
                    .user(user)
                    .refreshToken(refreshTokenValue)
                    .expiresAt(expiresAt)
                    .build();
            refreshTokenRepository.save(refreshToken);

            jwtCookieProvider.createRefreshTokenCookie(response, refreshTokenValue);

            log.info("[Auth] 소셜 로그인 토큰 발급 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.error("[Auth] 소셜 로그인 토큰 발급 중 서버 오류 발생 - userId: {}", userId, e);
            throw e;
        }
    }
}
