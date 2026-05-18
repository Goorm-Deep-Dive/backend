package org.accompany.backend.domain.calendar.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PendingTaskCalendarReq(

		@NotNull(message = "체크리스트 ID는 필수입니다.")
		Long userProcedureChecklistId,

		@NotNull(message = "일정 날짜는 필수입니다.")
		@FutureOrPresent(message = "현재 이후 날짜만 가능합니다.")
		LocalDateTime scheduledAt

) {
}