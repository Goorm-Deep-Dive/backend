package org.accompany.backend.domain.chat.dto.external;

import java.time.LocalDate;

public record AiChatContext(
        LocalDate dateOfDeath,
        AiChecklistSummary checklistSummary
) {
}
