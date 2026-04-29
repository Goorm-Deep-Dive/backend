package org.accompany.backend.domain.checklist.dto.response;

import java.util.List;

public record ChecklistCategoryProcedureRes(
			Long procedureCategoryId,
			String categoryName,
			List<Procedure> procedures
	) {
		public record Procedure(
				Long userProcedureChecklistId,
				Long procedureId,
				String procedureName,
				Integer remainingDays,
				boolean checked,
				String priority
		) {}
	}