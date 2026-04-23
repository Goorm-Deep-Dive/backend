package org.accompany.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {

    GOOGLE("구글"),
    KAKAO("카카오"),
    NAVER("네이버");

    private final String label;

    public static Provider fromRegistrationId(String registrationId) {
        try {
            return Provider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + registrationId, e);
        }
    }
}
