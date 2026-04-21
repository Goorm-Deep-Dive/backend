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
}