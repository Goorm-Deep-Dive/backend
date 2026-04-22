package org.accompany.backend.global.security.oauth.userinfo;

import org.accompany.backend.domain.user.entity.Provider;

import java.util.Map;

/**
 * 네이버 소셜로그인 응답 구조를 공통 형식으로 변환
 */
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public Provider getProvider() {
        return Provider.NAVER;
    }

    @Override
    public String getProviderId() {
        Object id = response.get("id");
        return id != null ? id.toString() : null;
    }

    @Override
    public String getEmail() {
        Object email = response.get("email");
        return email != null ? email.toString() : null;
    }

    @Override
    public String getName() {
        Object name = response.get("name");
        return name != null ? name.toString() : null;
    }
}
