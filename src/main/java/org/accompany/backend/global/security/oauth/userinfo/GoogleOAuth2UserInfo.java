package org.accompany.backend.global.security.oauth.userinfo;

import org.accompany.backend.domain.user.entity.Provider;

import java.util.Map;

/**
 * 구글 소셜로그인 응답 구조를 공통 형식으로 변환
 */
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }

    @Override
    public String getProviderId() {
        Object sub = attributes.get("sub");
        return sub != null ? sub.toString() : null;
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        return email != null ? email.toString() : null;
    }

    @Override
    public String getName() {
        Object name = attributes.get("name");
        return name != null ? name.toString() : null;
    }
}
