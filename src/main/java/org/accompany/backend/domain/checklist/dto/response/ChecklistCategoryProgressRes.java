package org.accompany.backend.domain.checklist.dto.response;

public record ChecklistCategoryProgressRes(
        Long categoryId,
        String categoryName,
        Integer progressRate,
        Integer totalCount,
        Integer completedCount
) {
}
