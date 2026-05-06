package org.accompany.backend.domain.chat.dto.response;

import org.accompany.backend.domain.chat.entity.ChatRole;

import java.time.LocalDateTime;

public record ChatMessageRes(
        ChatRole role,
        String content,
        LocalDateTime createdAt
) {
}
