package org.accompany.backend.domain.chat.dto.external;

import java.util.List;

public record AiChecklistSummary(
        List<String> notCompleted,
        List<String> urgent,
        List<String> completed
) {
}