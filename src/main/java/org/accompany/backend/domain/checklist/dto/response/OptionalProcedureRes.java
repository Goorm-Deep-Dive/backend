package org.accompany.backend.domain.checklist.dto.response;

import org.accompany.backend.domain.procedure.entity.Procedure;

public record OptionalProcedureRes(
		Long procedureId,
		String procedureName,
		Long categoryId,
		String categoryName,
		Integer remainingDays,
		String priority

) {
	public static OptionalProcedureRes from(
			Procedure p, Integer remainingDays, String priority) {
		return new OptionalProcedureRes(
				p.getProcedureId(),
				p.getProcedureName(),
				p.getProcedureCategory().getProcedureCategoryId(),
				p.getProcedureCategory().getCategoryName(),
				remainingDays,
				priority
		);
	}
}