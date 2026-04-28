package org.accompany.backend.domain.chat.dto.external;

public record AiChatMessage(
        String role,
        String content
) {
}
