package org.accompany.backend.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.repository.CalendarEventRepository;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 외에 추가시 readOnly 제거
@Slf4j
public class CalendarServiceImpl implements CalendarService {

	private final CalendarEventRepository calendarEventRepository;

	@Override
	public List<CalendarEventRes> getMonthlyEventsByUser(Long userId, int year, int month) {

		log.info(
				"=== CalendarServiceImpl.getMonthlyEventsByUser === userId: {}, year: {}, month: {}",
				userId, year, month );

		YearMonth yearMonth = YearMonth.of(year, month);

		LocalDateTime startOfMonth =
				yearMonth.atDay(1).atStartOfDay();

		LocalDateTime endOfMonth =
				yearMonth.atEndOfMonth().atTime(23, 59, 59);

		List<CalendarEventRes> result =
				calendarEventRepository
						.findByUserIdAndDateRange(
								userId,
								startOfMonth,
								endOfMonth
						)
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

		log.info(
				"=== CalendarServiceImpl.getMonthlyEventsByUser === userId: {}, count: {}",
				userId, result.size()
		);

		return result;
	}

	@Override
	public List<CalendarEventRes> getDailyEventsByUser(Long userId, String dateStr) {
		log.info(
				"=== CalendarServiceImpl.getDailyEventsByUser 시작 === userId: {}, date: {}",
				userId, dateStr );

		LocalDate date = LocalDate.parse(dateStr);

		LocalDateTime startOfDay =
				date.atStartOfDay();

		LocalDateTime endOfDay =
				date.atTime(23, 59, 59);

		List<CalendarEventRes> result =
				calendarEventRepository
						.findByUserIdAndDateRange(
								userId,
								startOfDay,
								endOfDay
						)
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

		log.info(
				"=== CalendarServiceImpl.getDailyEventsByUser 종료 === userId: {}, count: {}",
				userId, result.size() );

		return result;


	}

	@Override
	@PreAuthorize("@calendarSecurity.isOwner(principal.userId, #deceasedProfileId)")
	public List<CalendarEventRes> getMonthlyEventsByDeceasedProfile(
			CustomUserPrincipal principal,
			Long deceasedProfileId,
			int year,
			int month
	) {

		log.info(
				"=== CalendarServiceImpl.getMonthlyEventsByDeceasedProfile 시작 === userId: {}, deceasedProfileId: {}, year: {}, month: {}",
				principal.getUserId(),
				deceasedProfileId,
				year,
				month
		);

		YearMonth yearMonth = YearMonth.of(year, month);

		LocalDateTime startOfMonth =
				yearMonth.atDay(1).atStartOfDay();

		LocalDateTime endOfMonth =
				yearMonth.atEndOfMonth().atTime(23, 59, 59);

		List<CalendarEventRes> result =
				getEvents(deceasedProfileId, startOfMonth, endOfMonth);

		log.info(
				"=== CalendarServiceImpl.getMonthlyEventsByDeceasedProfile 종료 === deceasedProfileId: {}, 조회된 이벤트 수: {}",
				deceasedProfileId,
				result.size()
		);

		return result;
	}

	@Override
	@PreAuthorize("@calendarSecurity.isOwner(principal.userId, #deceasedProfileId)")
	public List<CalendarEventRes> getDailyEventsByDeceasedProfile(
			CustomUserPrincipal principal,
			Long deceasedProfileId,
			String dateStr
	) {

		log.info(
				"=== CalendarServiceImpl.getDailyEventsByDeceasedProfile 시작 === userId: {}, deceasedProfileId: {}, date: {}",
				principal.getUserId(),
				deceasedProfileId,
				dateStr
		);

		LocalDate date = LocalDate.parse(dateStr);

		LocalDateTime startOfDay = date.atStartOfDay();

		LocalDateTime endOfDay = date.atTime(23, 59, 59);

		List<CalendarEventRes> result =
				getEvents(deceasedProfileId, startOfDay, endOfDay);

		log.info(
				"=== CalendarServiceImpl.getDailyEventsByDeceasedProfile 종료 === deceasedProfileId: {}, 조회된 이벤트 수: {}",
				deceasedProfileId,
				result.size()
		);

		return result;
	}

	/**
	 * 캘린더 이벤트 조회
	 */
	private List<CalendarEventRes> getEvents(
			Long deceasedProfileId,
			LocalDateTime start,
			LocalDateTime end
	) {

		return calendarEventRepository
				.findByDeceasedProfileIdAndDateRange(
						deceasedProfileId,
						start,
						end
				)
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
	 * CalendarEvent → CalendarEventRes
	 */
	private CalendarEventRes fromCalendarEvent(
			CalendarEvent event
	) {

		return new CalendarEventRes(
				event.getCalendarEventId(),

				event.getDeceasedProfile() != null
						? event.getDeceasedProfile().getDeceasedProfileId()
						: null,

				event.getDeceasedProfile() != null
						? event.getDeceasedProfile().getName()
						: null,

				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist().getUserProcedureChecklistId()
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