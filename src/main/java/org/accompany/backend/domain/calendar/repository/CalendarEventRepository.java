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
	 * 사용자의 특정 기간 캘린더 이벤트 조회
	 * 기간이 겹치는 모든 이벤트 조회
	 */
	@Query("""
        SELECT ce
        FROM CalendarEvent ce
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

	/**
	 * Google Event ID 로 조회
	 */
	Optional<CalendarEvent> findByGoogleEventId(String googleEventId);

	/**
	 * 체크리스트 기반 일정 조회
	 */
	Optional<CalendarEvent> findByUserProcedureChecklistUserProcedureChecklistId(
			Long userProcedureChecklistId
	);

	/**
	 * 사용자 + 이벤트 타입 조회
	 */
	List<CalendarEvent> findByUserUserIdAndEventType(
			Long userId,
			EventType eventType
	);

	/**
	 * 사용자 구글 일정 전체 삭제
	 */
	void deleteByUserUserIdAndEventType(
			Long userId,
			EventType eventType
	);

	/**
	 * 체크리스트 기반 일정 삭제
	 */
	void deleteByUserProcedureChecklistUserProcedureChecklistId(
			Long userProcedureChecklistId
	);
}