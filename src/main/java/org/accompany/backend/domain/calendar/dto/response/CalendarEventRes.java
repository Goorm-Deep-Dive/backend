package org.accompany.backend.domain.calendar.dto.response;

import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.entity.EventType;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.procedure.entity.ProcedureCategory;

import java.time.LocalDateTime;

public record CalendarEventRes(
		Long eventId,        // calendar_event_id
		Long deceasedProfileId,
		String deceasedName,
		Long userProcedureChecklistId,
		String title,
		String description,
		LocalDateTime startAt,
		LocalDateTime endAt,
		Long procedureCategoryId,
		String category,  // procedure_category name (체크리스트만)
		String categoryColor,   // procedure_category color (체크리스트만)
		EventType eventType,       // "CHECKLIST", "GOOGLE", "USER_CUSTOM"
		Boolean checked
) {

	public static CalendarEventRes from(
			CalendarEvent event
	) {

		UserProcedureChecklist checklist = event.getUserProcedureChecklist();

		Procedure procedure = checklist != null ? checklist.getProcedure() : null;
		ProcedureCategory category = procedure != null ? procedure.getProcedureCategory() : null;

		DeceasedProfile profile = event.getDeceasedProfile();

		return new CalendarEventRes(

				event.getCalendarEventId(),
				profile != null ? profile.getDeceasedProfileId() : null,
				profile != null ? profile.getName() : null,
				checklist != null ? checklist.getUserProcedureChecklistId() : null,
				event.getTitle(),
				event.getDescription(),
				event.getStartAt(),
				event.getEndAt(),
				category != null ? category.getProcedureCategoryId() : null,
				category != null ? category.getCategoryName() : null,
				category != null ? category.getColor() : null,
				event.getEventType(),
				checklist != null && checklist.isChecked()
		);
	}
}