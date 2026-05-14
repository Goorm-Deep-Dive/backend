package org.accompany.backend.domain.calendar.service;

import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;

import java.util.List;

public interface CalendarService {

	/**
	 * 현재 활성 고인 프로필 기준 월별 이벤트 조회
	 */
	List<CalendarEventRes> getMonthlyEvents(
			Long userId,
			int year,
			int month
	);

	/**
	 * 현재 활성 고인 프로필 기준 일별 이벤트 조회
	 */
	List<CalendarEventRes> getDailyEvents(
			Long userId,
			String dateStr
	);
}