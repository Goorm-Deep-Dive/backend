package org.accompany.backend.domain.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.domain.calendar.service.CalendarService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Calendar API", description = "달력 API")
@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;

	/**
	 * 월별 캘린더 이벤트 조회
	 * GET /api/v1/calendar?year=2033&month=6
	 */
	@GetMapping
	@Operation(summary = "월별 캘린더 조회", description = "체크리스트에서 생성된 due date를 기반으로 월간 달력을 조회합니다.")
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getMonthlyEvents(
			@AuthenticationPrincipal Long userId,
			@RequestParam int year,
			@RequestParam int month
	) {

		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getMonthlyEvents(userId, year, month)
		);

	}

	/**
	 * 일별 캘린더 이벤트 조회
	 * GET /api/v1/calendar/daily?date=2024-06-15
	 */
	@Operation(summary = "일별 캘린더 조회", description = "체크리스트에서 생성된 due date를 기반으로 일간 달력을 조회합니다. 입력예시: yyyy-MM-dd 형식")
	@GetMapping("/daily")
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getDailyEvents(
			@AuthenticationPrincipal Long userId,
			@RequestParam String date  // yyyy-MM-dd
	) {
		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getDailyEvents(userId, date)
		);

	}
}