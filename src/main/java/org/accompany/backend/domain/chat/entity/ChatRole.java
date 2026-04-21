package org.accompany.backend.domain.chat.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatRole {

    USER("사용자"),
    AI("AI");

    private final String label;
}
