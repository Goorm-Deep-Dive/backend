package org.accompany.backend.domain.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.domain.calendar.service.CalendarService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Calendar API", description = "달력 API")
@RestController
@RequestMapping("/api/v1/calendars")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;

	/**
	 * 월별 캘린더 이벤트 조회
	 * GET /api/v1/calendar/{deceasedProfileId}?year=2033&month=6
	 */
	@GetMapping("/{deceasedProfileId}")
	@Operation(
			summary = "월별 캘린더 조회",
			description = "특정 고인 프로필의 월간 달력을 조회합니다. (입력: year - yyyy | month - MM 형식)"
	)
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getMonthlyEvents(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long deceasedProfileId,
			@RequestParam int year,
			@RequestParam int month
	) {

		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getMonthlyEvents(
						deceasedProfileId,
						year,
						month
				)
		);
	}

	/**
	 * 일별 캘린더 이벤트 조회
	 * GET /api/v1/calendar/{deceasedProfileId}/daily?date=2033-03-03
	 */
	@GetMapping("/{deceasedProfileId}/daily")
	@Operation(
			summary = "일별 캘린더 조회",
			description = "특정 고인 프로필의 일간 달력을 조회합니다. (입력: yyyy-MM-dd 형식)"
	)
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getDailyEvents(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long deceasedProfileId,
			@RequestParam String date
	) {

		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getDailyEvents(
						deceasedProfileId,
						date
				)
		);
	}
}