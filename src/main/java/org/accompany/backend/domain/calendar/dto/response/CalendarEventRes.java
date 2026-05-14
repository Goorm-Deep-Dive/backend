package org.accompany.backend.domain.calendar.dto.response;

import org.accompany.backend.domain.calendar.entity.EventType;

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
}