package org.accompany.backend.domain.calendar.service;

import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;

import java.util.List;

public interface CalendarService {

	/**
	 * 사용자 전체 월간 캘린더 조회
	 */
	List<CalendarEventRes> getMonthlyEventsByUser( Long userId, int year, int month );

	/**
	 * 사용자 전체 일간 캘린더 조회
	 */
	List<CalendarEventRes> getDailyEventsByUser( Long userId, String date );

	/**
	 * 특정 고인 프로필 월간 조회
	 */
	List<CalendarEventRes> getMonthlyEventsByDeceasedProfile( CustomUserPrincipal principal, Long deceasedProfileId, int year, int month );

	/**
	 * 특정 고인 프로필 일간 조회
	 */
	List<CalendarEventRes> getDailyEventsByDeceasedProfile( CustomUserPrincipal principal, Long deceasedProfileId, String date );
}