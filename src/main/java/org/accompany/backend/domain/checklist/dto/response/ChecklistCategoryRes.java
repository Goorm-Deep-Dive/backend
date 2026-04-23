package org.accompany.backend.domain.checklist.dto.response;

import java.util.List;

public record ChecklistCategoryRes(
		List<Category> categories
) {
	public record Category(
			Long procedureCategoryId,
			String categoryName
	) {}
}
