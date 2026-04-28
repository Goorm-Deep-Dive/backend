package org.accompany.backend.domain.chat.dto.external;

import java.util.List;

public record AiChatReq(
        Long userId,
        String message,
        AiChatContext context,
        List<AiChatMessage> history
) {
}
