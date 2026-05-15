package org.accompany.backend.domain.calendar.dto.response;

import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;

public record PendingTaskRes(

		Long userProcedureChecklistId,
		Long procedureId,
		String title
) {

	public static PendingTaskRes from(UserProcedureChecklist checklist) {

		return new PendingTaskRes(
				checklist.getUserProcedureChecklistId(),
				checklist.getProcedure().getProcedureId(),
				checklist.getProcedure().getProcedureName());
	}
}