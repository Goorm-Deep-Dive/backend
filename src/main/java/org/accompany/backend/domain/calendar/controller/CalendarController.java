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

	// =====================================================
	// 1. USER 전체 - 월간 및 일간
	// =====================================================

	/**
	 * 사용자 전체 캘린더 (월간 통합 조회)
	 * GET /api/v1/calendars?year=2033&month=3
	 */
	@GetMapping
	@Operation(summary = "사용자 전체 월간 캘린더 조회", description = "체크리스트에서 생성된 due date를 기반으로 사용자가 가진 모든 고인 프로필의 캘린더 이벤트를 통합 조회합니다. 입력예시: year: 조회 연도 (yyyy) | month: MM)")
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getMonthlyEventsByUser(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestParam int year,
			@RequestParam int month
	) {
		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getMonthlyEventsByUser(principal.getUserId(), year, month)
		);
	}

	/**
	 * 일별 캘린더 이벤트 조회
	 * GET /api/v1/calendars/daily?date=2033-03-03
	 */
	@Operation(summary = "사용자 전체 일간 캘린더 조회", description = "체크리스트에서 생성된 due date를 기반으로 특정 날짜 기준으로 사용자가 가진 모든 고인 프로필의 캘린더 이벤트를 통합 조회합니다. 입력예시: yyyy-MM-dd 형식")
	@GetMapping("/daily")
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getDailyEventsByUser(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestParam String date  // yyyy-MM-dd
	) {
		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getDailyEventsByUser(principal.getUserId(), date));
	}

	// =====================================================
	// 2. DECEASED_PROFILE - 월간 및 일간
	// =====================================================
	/**
	 * 월별 캘린더 이벤트 조회
	 * GET /api/v1/calendars/{deceasedProfileId}?year=2033&month=6
	 */
	@GetMapping("/{deceasedProfileId}")
	@Operation(
			summary = "고인 프로필 월간 캘린더 조회",
			description = "특정 고인 프로필 기준으로 해당 프로필의 캘린더 이벤트를 조회합니다. (입력: year - yyyy | month - MM 형식)"
	)
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getMonthlyEventsByDeceasedProfile(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long deceasedProfileId,
			@RequestParam int year,
			@RequestParam int month
	) {

		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getMonthlyEventsByDeceasedProfile(
						principal,
						deceasedProfileId,
						year,
						month
				)
		);
	}

	/**
	 * 일별 캘린더 이벤트 조회
	 * GET /api/v1/calendars/{deceasedProfileId}/daily?date=2033-03-03
	 */
	@GetMapping("/{deceasedProfileId}/daily")
	@Operation(
			summary = "고인 프로필 일간 캘린더 조회",
			description = " 특정 고인 프로필 기준으로 해당 날짜의 캘린더 이벤트를 조회합니다. (입력: yyyy-MM-dd 형식)"
	)
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getDailyEventsByDeceasedProfile(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long deceasedProfileId,
			@RequestParam String date
	) {

		return ApiResponse.success(
				SuccessCode.OK,
				calendarService.getDailyEventsByDeceasedProfile(
						principal,
						deceasedProfileId,
						date
				)
		);
	}
}