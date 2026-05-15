package org.accompany.backend.domain.calendar.dto.response;

import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.procedure.entity.DueDateType;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.survey.entity.SurveyRequirementType;

import java.util.Objects;

public record PendingTaskRes(

		Long userProcedureChecklistId,
		Long procedureId,
		String title,
		DueDateType dueDateType,
		String dueDateCategory, // Immediate 긴급, none 빠른처리
		Long procedureCategoryId,
		String category, // procedure_category name
		String color,
		String priority

) {

	public static PendingTaskRes from(UserProcedureChecklist checklist) {

		Procedure procedure = checklist.getProcedure();

		return new PendingTaskRes(
				checklist.getUserProcedureChecklistId(),
				checklist.getProcedure().getProcedureId(),
				checklist.getProcedure().getProcedureName(),

				procedure.getDueDateType(),
				convertCategory(procedure.getDueDateType()),

				procedure.getProcedureCategory().getProcedureCategoryId(),
				procedure.getProcedureCategory().getCategoryName(),
				procedure.getProcedureCategory().getColor(),
				convertPriority(procedure.getPriority())

		);
	}

	private static String convertPriority(Integer priority) {
		return Objects.equals(priority, 1)
				? SurveyRequirementType.REQUIRED.getLabel()
				: SurveyRequirementType.OPTIONAL.getLabel();
	}

	private static String convertCategory(DueDateType dueDateType) {
		return dueDateType == DueDateType.IMMEDIATE
				? "긴급" //DueDateType.IMMEDIATE.getLabel();
				: "빠른처리"; //DueDateType.NONE.getLabel();
	}

}