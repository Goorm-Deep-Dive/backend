package org.accompany.backend.domain.calendar.repository;

import org.accompany.backend.domain.calendar.entity.CalendarEvent;
import org.accompany.backend.domain.calendar.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

	/**
	 * 특정 고인 프로필의 기간별 캘린더 이벤트 조회
	 * 기간이 겹치는 모든 이벤트 조회
	 */
	@Query("""
			    SELECT ce
			    FROM CalendarEvent ce
			
			    LEFT JOIN FETCH ce.deceasedProfile dp
			    LEFT JOIN FETCH ce.userProcedureChecklist upc
			    LEFT JOIN FETCH upc.procedure p
			    LEFT JOIN FETCH p.procedureCategory pc
			
			    WHERE dp.deceasedProfileId = :deceasedProfileId
			      AND ce.startAt <= :endDate
			      AND (ce.endAt IS NULL OR ce.endAt >= :startDate)
			
			    ORDER BY ce.startAt ASC
			""")
	List<CalendarEvent> findByDeceasedProfileIdAndDateRange(
			@Param("deceasedProfileId") Long deceasedProfileId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);


	@Query("""
			    SELECT ce
			    FROM CalendarEvent ce
			
			    LEFT JOIN FETCH ce.deceasedProfile dp
			    LEFT JOIN FETCH ce.userProcedureChecklist upc
			    LEFT JOIN FETCH upc.procedure p
			    LEFT JOIN FETCH p.procedureCategory pc
			
			    WHERE ce.user.userId = :userId
			      AND ce.startAt <= :endDate
			      AND (ce.endAt IS NULL OR ce.endAt >= :startDate)
			
			    ORDER BY ce.startAt ASC
			""")
	List<CalendarEvent> findByUserIdAndDateRange(
			@Param("userId") Long userId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);

	@Query("""
			    select ce.userProcedureChecklist.userProcedureChecklistId
			    from CalendarEvent ce
			    where ce.deceasedProfile.deceasedProfileId = :deceasedProfileId
			      and ce.userProcedureChecklist is not null
			""")
	Set<Long> findChecklistIdsByDeceasedProfileId(
			@Param("deceasedProfileId") Long deceasedProfileId
	);


	/**
	 * Google Event ID 조회
	 */
	Optional<CalendarEvent> findByGoogleEventId(String googleEventId);

	/**
	 * 체크리스트 일정 존재 여부
	 */
	boolean existsByUserProcedureChecklist_UserProcedureChecklistId(
			Long userProcedureChecklistId
	);

	/**
	 * 특정 고인 프로필 + 이벤트 타입 존재 여부
	 */
	boolean existsByDeceasedProfile_DeceasedProfileIdAndEventType(
			Long deceasedProfileId,
			EventType eventType
	);

	/**
	 * 체크리스트 기반 일정 조회
	 */
	Optional<CalendarEvent> findByUserProcedureChecklistUserProcedureChecklistId(
			Long userProcedureChecklistId
	);

	/**
	 * 특정 고인 프로필 + 이벤트 타입 조회
	 */
	List<CalendarEvent> findByDeceasedProfileDeceasedProfileIdAndEventType(
			Long deceasedProfileId,
			EventType eventType
	);

	/**
	 * 특정 고인 프로필의 특정 타입 일정 전체 삭제
	 */
	void deleteByDeceasedProfileDeceasedProfileIdAndEventType(
			Long deceasedProfileId,
			EventType eventType
	);

	/**
	 * 체크리스트 기반 일정 삭제
	 */
	void deleteByUserProcedureChecklistUserProcedureChecklistId(
			Long userProcedureChecklistId
	);
}