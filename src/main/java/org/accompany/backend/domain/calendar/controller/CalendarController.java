package org.accompany.backend.domain.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.calendar.dto.request.PendingTaskCalendarReq;
import org.accompany.backend.domain.calendar.dto.response.CalendarEventRes;
import org.accompany.backend.domain.calendar.dto.response.PendingTaskRes;
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
	 * 월별 캘린더 조회
	 * 현재 활성화된 고인 프로필(activeDeceasedProfile) 기준
	 * <p>
	 * GET /api/v1/calendars?year=2033&month=05
	 */
	@GetMapping
	@Operation(
			summary = "월별 캘린더 조회",
			description = "현재 활성화된 고인 프로필 기준 월간 캘린더를 조회합니다. (입력: year-2033 | month-05)"
	)
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getMonthlyEvents(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestParam int year, @RequestParam int month) {

		return ApiResponse.success(SuccessCode.OK, calendarService.getMonthlyEvents(principal.getUserId(), year, month));
	}

	/**
	 * 일별 캘린더 조회
	 * 현재 활성화된 고인 프로필(activeDeceasedProfile) 기준
	 * <p>
	 * GET /api/v1/calendars/daily?date=2033-03-03
	 */
	@GetMapping("/daily")
	@Operation(
			summary = "일별 캘린더 조회",
			description = "현재 활성화된 고인 프로필 기준 일간 캘린더를 조회합니다. (입력: yyyy-MM-dd 형식)"
	)
	public ResponseEntity<ApiResponse<List<CalendarEventRes>>> getDailyEvents(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestParam String date
	) {

		return ApiResponse.success(SuccessCode.OK, calendarService.getDailyEvents(principal.getUserId(), date));
	}

	@Operation(summary = "처리 필요 과업 목록 조회", description = "아직 캘린더에 추가되지 않은 과업 목록을 조회합니다.")
	@GetMapping("/pending-tasks")
	public ResponseEntity<ApiResponse<List<PendingTaskRes>>> getPendingTasks(
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {

		return ApiResponse.success(SuccessCode.OK,
				calendarService.getPendingTasks(principal.getUserId())
		);
	}

	@Operation(summary = "미등록 일정 추가", description = "due date가 없는 처리 필요 과업을 캘린더 일정으로 추가합니다.")
	@PostMapping("/pending-tasks")
	public ResponseEntity<ApiResponse<CalendarEventRes>> createPendingTaskCalendar(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestBody @Valid PendingTaskCalendarReq pendingTaskCalendarReq
	) {

		return ApiResponse.success(SuccessCode.PENDING_TASK_CALENDAR_CREATED,
				calendarService.createPendingTaskCalendar(principal.getUserId(), pendingTaskCalendarReq)
		);
	}

	@Operation(summary = "처리 필요 과업 일정 수정")
	@PatchMapping("/pending-tasks/{eventId}")
	public ResponseEntity<ApiResponse<CalendarEventRes>> updatePendingTaskCalendar(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable Long eventId,
			@RequestBody @Valid PendingTaskCalendarReq pendingTaskCalendarReq
	) {

		return ApiResponse.success(
				SuccessCode.PENDING_TASK_CALENDAR_UPDATED,
				calendarService.updatePendingTaskCalendar(principal.getUserId(), eventId, pendingTaskCalendarReq)
		);
	}

	@Operation(summary = "처리 필요 과업 일정 삭제")
	@DeleteMapping("/pending-tasks/{eventId}")
	public ResponseEntity<ApiResponse<Void>> deletePendingTaskCalendar(
			@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long eventId) {

		calendarService.deletePendingTaskCalendar(principal.getUserId(), eventId);

		return ApiResponse.success(SuccessCode.PENDING_TASK_CALENDAR_DELETED);
	}

}