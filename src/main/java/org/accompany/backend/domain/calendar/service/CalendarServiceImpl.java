package org.accompany.backend.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.dto.request.PendingTaskCalendarReq;
import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.domain.calendar.dto.response.PendingTaskRes;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.entity.EventType;
import org.accompany.backend.domain.calendar.repository.CalendarEventRepository;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.procedure.entity.DueDateType;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
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
	private final UserRepository userRepository;
	private final UserProcedureChecklistRepository userProcedureChecklistRepository;


	@Override
	public List<CalendarEventRes> getMonthlyEvents(Long userId, int year, int month) {

		log.info("[Calendar] 월별 조회 시작 - userId={}, year={}, month={}", userId, year, month);

		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		DeceasedProfile deceasedProfile = user.getActiveDeceasedProfile();

		if (deceasedProfile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		YearMonth yearMonth = YearMonth.of(year, month);

		LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();

		LocalDateTime endOfMonth =
				yearMonth.atEndOfMonth().atTime(23, 59, 59);

		List<CalendarEventRes> result =
				getEvents(deceasedProfile.getDeceasedProfileId(), startOfMonth, endOfMonth);

		log.info("[Calendar] 월별 조회 종료 - deceasedProfileId={}, count={}", deceasedProfile.getDeceasedProfileId(), result.size());

		return result;
	}

	@Override
	public List<CalendarEventRes> getDailyEvents(
			Long userId, String dateStr
	) {

		log.info(
				"[Calendar] 일별 조회 시작 - userId={}, date={}",
				userId, dateStr);

		User user = userRepository.findById(userId)
				.orElseThrow(() ->
						new BusinessException(ErrorCode.USER_NOT_FOUND));

		DeceasedProfile deceasedProfile =
				user.getActiveDeceasedProfile();

		if (deceasedProfile == null) {
			throw new BusinessException(
					ErrorCode.DECEASED_PROFILE_NOT_FOUND
			);
		}

		LocalDate date = LocalDate.parse(dateStr);

		LocalDateTime startOfDay =
				date.atStartOfDay();

		LocalDateTime endOfDay =
				date.atTime(23, 59, 59);

		List<CalendarEventRes> result =
				getEvents(
						deceasedProfile.getDeceasedProfileId(),
						startOfDay,
						endOfDay
				);

		log.info(
				"[Calendar] 일별 조회 종료 - deceasedProfileId={}, count={}",
				deceasedProfile.getDeceasedProfileId(), result.size()
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
				.findByDeceasedProfile_DeceasedProfileIdAndDateRange(
						deceasedProfileId,
						start,
						end
				)
				.stream()
				.map(CalendarEventRes::from)
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

	@Override
	@Transactional(readOnly = true)
	public List<PendingTaskRes> getPendingTasks(Long userId) {

		log.info("[calendar] 처리 필요 과업 조회 시작 - userId={}", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		DeceasedProfile profile = user.getActiveDeceasedProfile();

		if (profile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		List<UserProcedureChecklist> tasks =
				userProcedureChecklistRepository.findPendingTasks(
						profile.getDeceasedProfileId()
				);

		List<PendingTaskRes> result = tasks.stream()
				.map(PendingTaskRes::from)
				.toList();

		log.info("[calendar] 처리 필요 과업 조회 완료 - userId={}, profileId={}, pendingTaskCount={}", userId, profile.getDeceasedProfileId(), result.size());

		return result;
	}

	@Override
	@Transactional
	public CalendarEventRes createPendingTaskCalendar(Long userId, PendingTaskCalendarReq createPendingTaskCalendarReq) {
		log.info("[calendar] 처리 필요 과업 일정 생성 시작 - userId={}, checklistId={}", userId, createPendingTaskCalendarReq.userProcedureChecklistId());

		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		DeceasedProfile deceasedProfile = user.getActiveDeceasedProfile();

		if (deceasedProfile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		UserProcedureChecklist checklist =
				userProcedureChecklistRepository.findById(createPendingTaskCalendarReq.userProcedureChecklistId())
						.orElseThrow(() -> new BusinessException(ErrorCode.CHECKLIST_NOT_FOUND));

		// 본인 프로필 체크리스트 검증
		if (!checklist.getDeceasedProfile()
				.getDeceasedProfileId()
				.equals(deceasedProfile.getDeceasedProfileId())) {

			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		Procedure procedure = checklist.getProcedure();

		if (!(procedure.getDueDateType() == DueDateType.NONE
				|| procedure.getDueDateType() == DueDateType.IMMEDIATE)) {
			throw new BusinessException(ErrorCode.INVALID_PENDING_TASK);
		}

		// 이미 일정이 존재하는지 체크
		boolean alreadyExists = calendarEventRepository.existsByUserProcedureChecklist_UserProcedureChecklistId(checklist.getUserProcedureChecklistId());

		if (alreadyExists) {
			throw new BusinessException(ErrorCode.CALENDAR_EVENT_ALREADY_EXISTS);
		}

		CalendarEvent calendarEvent = CalendarEvent.builder()
				.user(user)
				.deceasedProfile(deceasedProfile)
				.userProcedureChecklist(checklist)
				.title(procedure.getProcedureName())
				.description(null)
				.startAt(createPendingTaskCalendarReq.scheduledAt())
				.endAt(createPendingTaskCalendarReq.scheduledAt().plusHours(1))
				.eventType(EventType.USER_CUSTOM)
				.build();

		CalendarEvent savedEvent = calendarEventRepository.save(calendarEvent);

		log.info("[calendar] 처리 필요 과업 일정 생성 완료 - userId={}, eventId={}", userId, savedEvent.getCalendarEventId());

		return CalendarEventRes.from(savedEvent);
	}

	@Override
	@Transactional
	public CalendarEventRes updatePendingTaskCalendar(Long userId, Long eventId, PendingTaskCalendarReq pendingTaskCalendarReq) {
		log.info("[calendar] 처리 필요 과업 일정 수정 시작 - userId={}, eventId={}", userId, eventId);

		CalendarEvent event = calendarEventRepository.findById(eventId)
				.orElseThrow(() ->
						new BusinessException(ErrorCode.CALENDAR_EVENT_NOT_FOUND)
				);

		if (!event.getUser().getUserId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		if (event.getEventType() != EventType.USER_CUSTOM) {
			throw new BusinessException(ErrorCode.CANNOT_MODIFY_SYSTEM_EVENT);
		}

		event.updateSchedule(
				pendingTaskCalendarReq.scheduledAt(),
				pendingTaskCalendarReq.scheduledAt().plusHours(1)
		);

		log.info("[calendar] 처리 필요 과업 일정 수정 완료 - userId={}, eventId={}", userId, eventId);

		return CalendarEventRes.from(event);
	}

	@Override
	@Transactional
	public void deletePendingTaskCalendar(Long userId, Long eventId) {
		log.info("[calendar] 처리 필요 과업 일정 삭제 시작 - userId={}, eventId={}", userId, eventId);

		CalendarEvent event = calendarEventRepository.findById(eventId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CALENDAR_EVENT_NOT_FOUND));

		if (!event.getUser().getUserId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		if (event.getEventType() != EventType.USER_CUSTOM) {
			throw new BusinessException(ErrorCode.CANNOT_DELETE_SYSTEM_EVENT);
		}

		calendarEventRepository.delete(event);

		log.info("[calendar] 처리 필요 과업 일정 삭제 완료 - userId={}, eventId={}", userId, eventId);
	}
}