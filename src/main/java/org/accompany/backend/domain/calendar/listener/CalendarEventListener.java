package org.accompany.backend.domain.calendar.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.entity.EventType;
import org.accompany.backend.domain.calendar.event.CalendarUpdatedEvent;
import org.accompany.backend.domain.calendar.repository.CalendarEventRepository;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.deceasedProfile.repository.DeceasedProfileRepository;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalendarEventListener {

	private final DeceasedProfileRepository deceasedProfileRepository;
	private final UserProcedureChecklistRepository checklistRepository;
	private final CalendarEventRepository calendarEventRepository;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleCalendarUpdated(CalendarUpdatedEvent event) {

		Long profileId = event.deceasedProfileId();

		log.info("[Calendar] 업데이트 시작 - profileId={}", profileId);

		DeceasedProfile profile = deceasedProfileRepository.findById(profileId)
				.orElseThrow(() -> new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND));

		User user = profile.getUser();

		syncChecklistEvents(user, profile);

		syncMemorialEvent(profile);

		log.info("[Calendar] 업데이트 종료 - profileId={}", profileId);
	}

	/**
	 * 체크리스트 일정 생성 / 수정
	 */
	private void syncChecklistEvents(User user, DeceasedProfile profile) {

		List<UserProcedureChecklist> checklists =
				checklistRepository.findByDeceasedProfile_DeceasedProfileId(profile.getDeceasedProfileId());

		for (UserProcedureChecklist checklist : checklists) {

			if (checklist.getDueDate() == null) {
				continue;
			}

			calendarEventRepository
					.findByUserProcedureChecklistUserProcedureChecklistId(checklist.getUserProcedureChecklistId())
					.ifPresentOrElse(

							existing ->
									existing.updateSchedule(
											checklist.getProcedure().getProcedureName(),
											checklist.getProcedure().getDescription(),
											checklist.getDueDate(),
											checklist.getDueDate().plusHours(1)
									),
							() -> calendarEventRepository.save(toChecklistEvent(user, profile, checklist)));
		}
	}

	/**
	 * 영면일 일정 생성 / 수정 / 삭제
	 */
	private void syncMemorialEvent(
			DeceasedProfile profile
	) {

		Optional<CalendarEvent> memorialEvent =
				calendarEventRepository.findByDeceasedProfileDeceasedProfileIdAndEventType(profile.getDeceasedProfileId(), EventType.MEMORIAL_DAY);

		if (profile.getDateOfDeath() == null) {
			memorialEvent.ifPresent(calendarEventRepository::delete);
			return;
		}

		memorialEvent.ifPresentOrElse(

				existing -> existing.updateSchedule(
						profile.getName() + " 영면일",
						"고인의 영면일",
						profile.getDateOfDeath().atStartOfDay(),
						profile.getDateOfDeath().atStartOfDay().plusHours(1)
				),

				() -> calendarEventRepository.save(
						CalendarEvent.builder()
								.user(profile.getUser())
								.deceasedProfile(profile)
								.title(profile.getName() + " 영면일")
								.description("고인의 영면일")
								.startAt(profile.getDateOfDeath().atStartOfDay())
								.endAt(profile.getDateOfDeath().atStartOfDay().plusHours(1))
								.eventType(EventType.MEMORIAL_DAY)
								.build()
				)
		);
	}

	private CalendarEvent toChecklistEvent(User user, DeceasedProfile profile, UserProcedureChecklist checklist) {

		return CalendarEvent.builder()
				.user(user)
				.deceasedProfile(profile)
				.userProcedureChecklist(checklist)
				.title(checklist.getProcedure().getProcedureName())
				.description(checklist.getProcedure().getDescription())
				.startAt(checklist.getDueDate())
				.endAt(checklist.getDueDate().plusHours(1))
				.eventType(EventType.CHECKLIST)
				.build();
	}
}