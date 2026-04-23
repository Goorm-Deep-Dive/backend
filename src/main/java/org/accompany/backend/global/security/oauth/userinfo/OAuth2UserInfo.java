package org.accompany.backend.global.security.oauth.userinfo;

import org.accompany.backend.domain.user.entity.Provider;

import java.util.Map;

/**
 * 소셜 로그인 공통 인터페이스
 */
public interface OAuth2UserInfo {
    Provider getProvider();
    String getProviderId();
    String getEmail();
    String getName();

    static OAuth2UserInfo of(Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
        };
    }
}
