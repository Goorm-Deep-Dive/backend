package org.accompany.backend.domain.chat.dto.external;

public record AiChatContext(
        String userName,
        AiChecklistSummary checklistSummary
) {
}
