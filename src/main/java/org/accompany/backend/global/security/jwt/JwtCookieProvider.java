package org.accompany.backend.global.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * RefreshToken을 HttpOnly 쿠키로 생성, 삭제, 조회하는 역할을 담당하며
 * 인증 재발급(refresh) 과정에서 쿠키 기반 토큰 처리를 지원하는 Provider 클래스.
 */
@Slf4j
@Component
public class JwtCookieProvider {

    @Value("${jwt.cookie.refresh-token-name}")
    private String refreshTokenCookieName;

    @Value("${jwt.cookie.refresh-token-max-age}")
    private int refreshTokenMaxAge;

    @Value("${jwt.cookie.secure}")
    private boolean secure;

    @Value("${jwt.cookie.same-site}")
    private String sameSite;

    /**
     * RefreshToken 쿠키 생성
     */
    public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        try {
            ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                    .httpOnly(true)
                    .secure(secure) // 개발 환경 기준
                    .path("/")
                    .maxAge(refreshTokenMaxAge)
                    .sameSite(sameSite)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        } catch (Exception e) {
            log.warn("[JWT] RefreshToken 쿠키 생성 실패: {}", e.getMessage());
        }
    }

    /**
     * RefreshToken 쿠키 삭제
     */
    public void deleteRefreshTokenCookie(HttpServletResponse response) {

        try {
            ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, "")
                    .httpOnly(true)
                    .secure(secure)
                    .path("/")
                    .maxAge(0)
                    .sameSite(sameSite)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        } catch (Exception e) {
            log.warn("[JWT] RefreshToken 쿠키 삭제 실패: {}", e.getMessage());
        }
    }

    /**
     * Request에서 RefreshToken 쿠키 조회
     */
    public String resolveRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        try {
            for (var cookie : request.getCookies()) {
                if (refreshTokenCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }

        } catch (Exception e) {
            log.warn("[JWT] RefreshToken 쿠키 조회 실패: {}", e.getMessage());
        }

        return null;
    }
}
