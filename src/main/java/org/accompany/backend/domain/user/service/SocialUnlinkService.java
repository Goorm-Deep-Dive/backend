package org.accompany.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialUnlinkService {

    private final RestClient restClient;

    @Value("${spring.security.oauth2.client.registration.naver.client-id:}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret:}")
    private String naverClientSecret;

    public void unlink(User user) {
        if (user == null) {
            return;
        }

        switch (user.getProvider()) {
            case GOOGLE -> unlinkGoogle(user);
            case KAKAO -> unlinkKakao(user);
            case NAVER -> unlinkNaver(user);
            default -> log.warn("[SocialUnlink] 지원하지 않는 provider: {}", user.getProvider());
        }
    }

    private void unlinkGoogle(User user) {
        try {
            String token = firstNotBlank(
                    user.getGoogleRefreshToken(),
                    user.getGoogleAccessToken(),
                    user.getProviderRefreshToken(),
                    user.getProviderAccessToken()
            );

            if (isBlank(token)) {
                log.warn("[SocialUnlink] Google token 없음. unlink 생략: userId={}", user.getUserId());
                return;
            }

            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("oauth2.googleapis.com")
                            .path("/revoke")
                            .queryParam("token", token)
                            .build())
                    .retrieve()
                    .toBodilessEntity();

            log.info("[SocialUnlink] Google unlink 성공: userId={}", user.getUserId());
        } catch (Exception e) {
            log.error("[SocialUnlink] Google unlink 실패: userId={}", user.getUserId(), e);
            throw new BusinessException(ErrorCode.SOCIAL_UNLINK_FAILED);
        }
    }

    private void unlinkKakao(User user) {
        try {
            if (!isBlank(user.getGoogleProviderUserId())) {
                unlinkGoogle(user);
            }

            if (isBlank(user.getProviderAccessToken())) {
                log.warn("[SocialUnlink] Kakao access token 없음. unlink 생략: userId={}", user.getUserId());
                return;
            }

            restClient.post()
                    .uri("https://kapi.kakao.com/v1/user/unlink")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getProviderAccessToken())
                    .retrieve()
                    .toBodilessEntity();

            log.info("[SocialUnlink] Kakao unlink 성공: userId={}", user.getUserId());
        } catch (Exception e) {
            log.error("[SocialUnlink] Kakao unlink 실패: userId={}", user.getUserId(), e);
            throw new BusinessException(ErrorCode.SOCIAL_UNLINK_FAILED);
        }
    }

    private void unlinkNaver(User user) {
        try {
            if (!isBlank(user.getGoogleProviderUserId())) {
                unlinkGoogle(user);
            }

            if (isBlank(user.getProviderAccessToken())) {
                log.warn("[SocialUnlink] Naver access token 없음. unlink 생략: userId={}", user.getUserId());
                return;
            }

            String encodedToken = URLEncoder.encode(
                    user.getProviderAccessToken(),
                    StandardCharsets.UTF_8
            );

            restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("nid.naver.com")
                            .path("/oauth2.0/token")
                            .queryParam("grant_type", "delete")
                            .queryParam("client_id", naverClientId)
                            .queryParam("client_secret", naverClientSecret)
                            .queryParam("access_token", encodedToken)
                            .queryParam("service_provider", "NAVER")
                            .build())
                    .retrieve()
                    .toBodilessEntity();

            log.info("[SocialUnlink] Naver unlink 성공: userId={}", user.getUserId());
        } catch (Exception e) {
            log.error("[SocialUnlink] Naver unlink 실패: userId={}", user.getUserId(), e);
            throw new BusinessException(ErrorCode.SOCIAL_UNLINK_FAILED);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }
}