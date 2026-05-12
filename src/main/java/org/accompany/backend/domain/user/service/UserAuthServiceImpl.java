package org.accompany.backend.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.RefreshToken;
import org.accompany.backend.domain.user.entity.Role;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.event.UserEvent;
import org.accompany.backend.domain.user.repository.RefreshTokenRepository;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.accompany.backend.global.security.dto.TokenRes;
import org.accompany.backend.global.security.jwt.JwtCookieProvider;
import org.accompany.backend.global.security.jwt.JwtTokenProvider;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * RefreshToken 검증 후 AccessToken 재발급
     */
    @Override
    @Transactional
    public TokenRes refresh(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtCookieProvider.resolveRefreshToken(request);

        if (token == null) {
            log.warn("[Auth] RefreshToken 없음 - 재발급 요청 실패");
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("[Auth] 유효하지 않은 RefreshToken - 재발급 요청 실패");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> {
                    log.warn("[Auth] 저장된 RefreshToken 조회 실패");
                    return new BusinessException(ErrorCode.STORED_REFRESH_TOKEN_NOT_FOUND);
                });

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("[Auth] 만료된 RefreshToken - userId: {}", refreshToken.getUser().getUserId());
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        User user = refreshToken.getUser();

        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(),
                user.getRole()
        );

        log.info("[Auth] AccessToken 재발급 완료 - userId: {}", user.getUserId());

        return new TokenRes(newAccessToken);
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
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

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
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("[Auth] 로그아웃 시작");

        String refreshToken = jwtCookieProvider.resolveRefreshToken(request);

        if(refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.findByRefreshToken(refreshToken)
                    .ifPresent(token -> {
                        Long userId = token.getUser().getUserId();

                        applicationEventPublisher.publishEvent(
                                UserEvent.logout(userId)
                        );

                        refreshTokenRepository.delete(token);
                    });
        }

        jwtCookieProvider.deleteRefreshTokenCookie(response);

        log.info("[Auth] 로그아웃 성공");
    }
}