package org.accompany.backend.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * AccessToken, RefreshToken 생성 및 검증을 담당하며
 * JWT 내부 사용자 식별 정보(userId, role)를 추출하는 Provider 클래스.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey key;

    /**
     * SecretKey 초기화
     */
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("[JWT] SecretKey 초기화 완료");
    }

    /**
     * AccessToken 생성
     */
    public String createAccessToken(Long userId, Role role) {

        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpiration)))
                .signWith(key)
                .compact();

        return token;
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Long userId) {

        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshTokenExpiration)))
                .signWith(key)
                .compact();

        return token;
    }

    /**
     * JWT 유효성 검증
     */
    public boolean validateToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            log.warn("[JWT] 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT에서 userId 추출
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT에서 role 추출 (AccessToken 전용)
     */
    public Role getRole(String token) {
        Claims claims = getClaims(token);

        return Role.valueOf(claims.get("role", String.class));
    }

    /**
     * JWT 만료시간 추출
     */
    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    /**
     * JWT Claims 추출 내부 메서드
     */
    private Claims getClaims(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            log.warn("[JWT] Claims 추출 실패: {}", e.getMessage());
            throw e;
        }
    }
}