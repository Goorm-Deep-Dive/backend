package org.accompany.backend.domain.chat.dto.external;

import java.time.LocalDate;
import java.util.List;

public record AiChecklistSummary(
        List<DueItem> notCompletedWithDeadline,
        List<String> notCompletedUrgent,
        List<String> completed,
        int notCompletedTotalCount
) {
    public record DueItem(String name, LocalDate dueDate) {}
}