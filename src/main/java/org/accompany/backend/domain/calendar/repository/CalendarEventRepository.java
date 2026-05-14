package org.accompany.backend.domain.calendar.repository;

import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

	/**
	 * 특정 고인 프로필의 기간별 캘린더 이벤트 조회
	 * 기간이 겹치는 이벤트 전체 조회
	 */
	@Query("""
			    SELECT ce
			    FROM CalendarEvent ce
			    LEFT JOIN FETCH ce.userProcedureChecklist upc
			    LEFT JOIN FETCH upc.procedure p
			    LEFT JOIN FETCH p.procedureCategory
			    LEFT JOIN FETCH ce.deceasedProfile dp
			    WHERE dp.deceasedProfileId = :deceasedProfileId
			      AND ce.startAt <= :endDate
			      AND (
			            ce.endAt IS NULL
			            OR ce.endAt >= :startDate
			      )
			    ORDER BY ce.startAt ASC
			""")
	List<CalendarEvent> findByDeceasedProfile_DeceasedProfileIdAndDateRange(
			@Param("deceasedProfileId") Long deceasedProfileId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);

	/**
	 * Google Event ID 조회
	 */
	Optional<CalendarEvent> findByGoogleEventId(String googleEventId);



	/**
	 * 체크리스트 기반 일정 조회
	 */
	Optional<CalendarEvent> findByUserProcedureChecklistUserProcedureChecklistId(
			Long userProcedureChecklistId
	);

	/**
	 * 특정 고인 프로필 + 이벤트 타입 조회
	 */
	Optional<CalendarEvent> findByDeceasedProfileDeceasedProfileIdAndEventType(
			Long deceasedProfileId,
			EventType eventType
	);

}