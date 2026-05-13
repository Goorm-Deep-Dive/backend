package org.accompany.backend.domain.calendar.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.entity.EventType;
import org.accompany.backend.domain.calendar.event.ChecklistCreatedEvent;
import org.accompany.backend.domain.calendar.repository.CalendarEventRepository;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.deceasedProfile.repository.DeceasedProfileRepository;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalendarEventListener {

	private final DeceasedProfileRepository deceasedProfileRepository;
	private final UserProcedureChecklistRepository userProcedureChecklistRepository;
	private final CalendarEventRepository calendarEventRepository;

	@EventListener
	@Transactional
	public void handleChecklistCreated(ChecklistCreatedEvent event) {

		Long deceasedProfileId = event.getDeceasedProfileId();

		log.info(
				"[Calendar] 체크리스트 기반 일정 생성 시작 - deceasedProfileId={}",
				deceasedProfileId
		);

		DeceasedProfile deceasedProfile = deceasedProfileRepository
				.findById(deceasedProfileId)
				.orElseThrow(() -> {
					log.error(
							"[Calendar] 고인 프로필 조회 실패 - deceasedProfileId={}",
							deceasedProfileId
					);
					return new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
				});

		User user = deceasedProfile.getUser();

		//OrderByDueDateAsc OrNOT
		List<UserProcedureChecklist> checklists =
				userProcedureChecklistRepository
						.findByDeceasedProfile_DeceasedProfileId(
								deceasedProfileId
						);

		log.info(
				"[Calendar] 체크리스트 조회 완료 - userId={}, deceasedProfileId={}, checklistCount={}",
				user.getUserId(),
				deceasedProfileId,
				checklists.size()
		);

		/**
		 * 이미 생성된 체크리스트 일정 조회
		 */
		Set<Long> existingChecklistIds = calendarEventRepository
				.findChecklistIdsByDeceasedProfileId(deceasedProfileId);

		log.info(
				"[Calendar] 기존 일정 조회 완료 - deceasedProfileId={}, existingEventCount={}",
				deceasedProfileId,
				existingChecklistIds.size()
		);

		/**
		 * dueDate 없는 체크리스트 제외
		 */
		long skippedCount = checklists.stream()
				.filter(c -> c.getDueDate() == null)
				.count();

		if (skippedCount > 0) {
			log.info(
					"[Calendar] dueDate null 체크리스트 제외 - userId={}, deceasedProfileId={}, skippedCount={}",
					user.getUserId(),
					deceasedProfileId,
					skippedCount
			);
		}

		/**
		 * 체크리스트 일정 생성
		 */
		List<CalendarEvent> calendarEvents = checklists.stream()

				// dueDate 없는 경우 제외
				.filter(c -> c.getDueDate() != null)

				// 이미 생성된 일정 제외
				.filter(c ->
						!existingChecklistIds.contains(
								c.getUserProcedureChecklistId()
						)
				)

				.map(c -> toChecklistEvent(user, deceasedProfile, c))
				.collect(Collectors.toList());

		/**
		 * 영면일 일정 생성
		 */
		createMemorialDayEventIfNeeded(user, deceasedProfile)
				.ifPresent(calendarEvents::add);

		log.info(
				"[Calendar] 저장 대상 일정 생성 완료 - userId={}, deceasedProfileId={}, eventCount={}",
				user.getUserId(),
				deceasedProfileId,
				calendarEvents.size()
		);

		if (!calendarEvents.isEmpty()) {

			log.info(
					"[Calendar] 캘린더 이벤트 저장 시작 - deceasedProfileId={}, eventCount={}",
					deceasedProfileId,
					calendarEvents.size()
			);

			calendarEventRepository.saveAll(calendarEvents);

			log.info(
					"[Calendar] 캘린더 이벤트 저장 완료 - deceasedProfileId={}, savedEventCount={}",
					deceasedProfileId,
					calendarEvents.size()
			);
		} else {

			log.info(
					"[Calendar] 저장할 캘린더 이벤트 없음 - deceasedProfileId={}",
					deceasedProfileId
			);
		}
	}

	private CalendarEvent toChecklistEvent(
			User user,
			DeceasedProfile deceasedProfile,
			UserProcedureChecklist checklist
	) {

		return CalendarEvent.builder()
				.user(user)
				.deceasedProfile(deceasedProfile)
				.userProcedureChecklist(checklist)
				.title(checklist.getProcedure().getProcedureName())
				.description(checklist.getProcedure().getDescription())
				.startAt(checklist.getDueDate())
				.endAt(checklist.getDueDate().plusHours(1))
				.eventType(EventType.CHECKLIST)
				.build();
	}

	private Optional<CalendarEvent> createMemorialDayEventIfNeeded(
			User user,
			DeceasedProfile deceasedProfile
	) {

		if (deceasedProfile.getDateOfDeath() == null) {

			log.info(
					"[Calendar] 영면일 없음 - memorial event 생성 스킵 - deceasedProfileId={}",
					deceasedProfile.getDeceasedProfileId()
			);

			return Optional.empty();
		}

		boolean exists = calendarEventRepository
				.existsByDeceasedProfile_DeceasedProfileIdAndEventType(
						deceasedProfile.getDeceasedProfileId(),
						EventType.MEMORIAL_DAY
				);

		if (exists) {

			log.info(
					"[Calendar] 영면일 일정 이미 존재 - deceasedProfileId={}",
					deceasedProfile.getDeceasedProfileId()
			);

			return Optional.empty();
		}

		log.info(
				"[Calendar] 영면일 일정 생성 - deceasedProfileId={}",
				deceasedProfile.getDeceasedProfileId()
		);

		return Optional.of(
				CalendarEvent.builder()
						.user(user)
						.deceasedProfile(deceasedProfile)
						.title(deceasedProfile.getName() + " 영면일")
						.description("고인 영면일 일정")
						.startAt(
								deceasedProfile.getDateOfDeath()
										.atStartOfDay()
						)
						.endAt(
								deceasedProfile.getDateOfDeath()
										.atStartOfDay()
										.plusHours(1)
						)
						.eventType(EventType.MEMORIAL_DAY)
						.build()
		);
	}
}