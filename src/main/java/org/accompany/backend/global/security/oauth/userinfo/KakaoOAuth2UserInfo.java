package org.accompany.backend.global.security.oauth.userinfo;

import org.accompany.backend.domain.user.entity.Provider;

import java.util.Map;

/**
 * 카카오 소셜로그인 응답 구조를 공통 형식으로 변환
 */
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {

        Object obj = attributes.get("kakao_account");

        if (!(obj instanceof Map<?, ?> kakaoAccount)) {
            return null;
        }

        Object email = kakaoAccount.get("email");

        return email == null ? null : email.toString();
    }

    @Override
    public String getName() {

        Object kakaoAccountObj = attributes.get("kakao_account");
        if (!(kakaoAccountObj instanceof Map<?, ?> kakaoAccount)) {
            return null;
        }

        Object profileObj = kakaoAccount.get("profile");
        if (!(profileObj instanceof Map<?, ?> profile)) {
            return null;
        }

        Object nickname = profile.get("nickname");
        return nickname == null ? null : String.valueOf(nickname);
    }
}
