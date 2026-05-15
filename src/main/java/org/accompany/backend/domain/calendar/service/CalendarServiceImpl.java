package org.accompany.backend.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.domain.calendar.dto.response.PendingTaskRes;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.repository.CalendarEventRepository;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
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
			Long deceasedProfileId, LocalDateTime start, LocalDateTime end
	) {

		return calendarEventRepository
				.findByDeceasedProfile_DeceasedProfileIdAndDateRange(
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
	 * CalendarEvent → Response
	 */
	private CalendarEventRes fromCalendarEvent(
			CalendarEvent event
	) {

		return new CalendarEventRes(
				event.getCalendarEventId(),

				// deceasedProfileId
				event.getDeceasedProfile() != null
						? event.getDeceasedProfile()
						.getDeceasedProfileId()
						: null,

				// deceasedName
				event.getDeceasedProfile() != null
						? event.getDeceasedProfile()
						.getName()
						: null,

				// userProcedureChecklistId
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.getUserProcedureChecklistId()
						: null,

				// title
				event.getTitle(),

				// description
				event.getDescription(),

				// startAt
				event.getStartAt(),

				// endAt
				event.getEndAt(),

				// procedureCategoryId
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.getProcedure()
						.getProcedureCategory()
						.getProcedureCategoryId()
						: null,

				// category
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.getProcedure()
						.getProcedureCategory()
						.getCategoryName()
						: null,

				// categoryColor
				event.getUserProcedureChecklist() != null
						? event.getUserProcedureChecklist()
						.getProcedure()
						.getProcedureCategory()
						.getColor()
						: null,

				// eventType
				event.getEventType(),

				// checked
				event.getUserProcedureChecklist() != null
						&& event.getUserProcedureChecklist()
						.isChecked()
		);
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

		log.info(
				"[calendar] 처리 필요 과업 조회 완료 - userId={}, profileId={}, pendingTaskCount={}",
				userId,
				profile.getDeceasedProfileId(),
				result.size()
		);

		return result;
	}
}