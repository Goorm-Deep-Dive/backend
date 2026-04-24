package org.accompany.backend.domain.checklist.dto.response;

import java.util.List;

public record ChecklistOverallProgressRes(
        Integer progressRate,
        Integer totalCount,
        Integer completedCount,
        List<ChecklistCategoryProgressRes> checklistCategoryProgressResList

) {
}
