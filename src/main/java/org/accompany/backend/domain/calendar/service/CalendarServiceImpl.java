package org.accompany.backend.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.entity.EventType;
import org.accompany.backend.domain.calendar.repository.CalendarEventRepository;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CalendarServiceImpl implements CalendarService {

	private final CalendarEventRepository calendarEventRepository;



	@Override
	public List<CalendarEventRes> getMonthlyEvents(
			Long userId,
			int year,
			int month
	) {

		YearMonth yearMonth = YearMonth.of(year, month);

		LocalDateTime startOfMonth =
				yearMonth.atDay(1).atStartOfDay();

		LocalDateTime endOfMonth =
				yearMonth.atEndOfMonth().atTime(23, 59, 59);

		return getEvents(userId, startOfMonth, endOfMonth);
	}

	@Override
	public List<CalendarEventRes> getDailyEvents(
			Long userId,
			String dateStr
	) {

		LocalDate date = LocalDate.parse(dateStr);

		LocalDateTime startOfDay = date.atStartOfDay();

		LocalDateTime endOfDay = date.atTime(23, 59, 59);

		return getEvents(userId, startOfDay, endOfDay);
	}

	/**
	 * 캘린더 이벤트 조회
	 */
	private List<CalendarEventRes> getEvents(
			Long userId,
			LocalDateTime start,
			LocalDateTime end
	) {

		return calendarEventRepository
				.findByUserIdAndDateRange(userId, start, end)
				.stream()
				.map(this::fromCalendarEvent)
				.sorted(
						Comparator.comparing(
								CalendarEventRes::startAt,
								Comparator.nullsLast(
										Comparator.naturalOrder()
								)
						)
				)
				.toList();
	}

	/**
	 * 체크리스트 → CalendarEventRes
	 */
	private CalendarEventRes fromChecklist(
			UserProcedureChecklist checklist
	) {

		return new CalendarEventRes(
				null,
				checklist.getUserProcedureChecklistId(),
				checklist.getProcedure().getProcedureName(),
				checklist.getProcedure().getDescription(),
				checklist.getDueDate(),
				checklist.getDueDate(),
				checklist.getProcedure()
						.getProcedureCategory()
						.getCategoryName(),
				checklist.getProcedure()
						.getProcedureCategory()
						.getColor(),
				EventType.CHECKLIST,
				checklist.isChecked()
		);
	}

	/**
	 * CalendarEvent → CalendarEventRes
	 */
	private CalendarEventRes fromCalendarEvent(
			CalendarEvent event
	) {

		return new CalendarEventRes(
				event.getCalendarEventId(),
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.getUserProcedureChecklistId()
						: null,
				event.getTitle(),
				event.getDescription(),
				event.getStartAt(),
				event.getEndAt(),
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.getProcedure()
						.getProcedureCategory()
						.getCategoryName()
						: null,
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.getProcedure()
						.getProcedureCategory()
						.getColor()
						: null,
				event.getEventType(),
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.isChecked()
						: null
		);
	}
}