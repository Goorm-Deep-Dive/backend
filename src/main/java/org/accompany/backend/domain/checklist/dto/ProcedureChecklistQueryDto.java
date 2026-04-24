package org.accompany.backend.domain.checklist.dto;

import org.accompany.backend.domain.procedure.entity.DueDateType;
import org.accompany.backend.domain.procedure.entity.DueDateUnit;

import java.time.LocalDateTime;

public record ProcedureChecklistQueryDto(
		Long procedureId,
		String procedureName,
		Long userProcedureChecklistId,
		Boolean isChecked,
		LocalDateTime dueDate,

		Integer baseDueDate,
		DueDateUnit dueDateUnit,
		DueDateType dueDateType
) {}