package org.accompany.backend.domain.checklist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProcedureRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistOverallProgressRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistProcedureDetailRes;
import org.accompany.backend.domain.checklist.service.ChecklistService;
import org.accompany.backend.domain.user.service.UserService;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.exception.BusinessException;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Checklist API", description = "체크리스트 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checklists")
public class ChecklistController {

	private final ChecklistService checklistService;

//	@SecurityRequirement(name = "BearerAuth")
	@GetMapping("/categories")
	@Operation(summary = "카테고리 목록 조회", description = "각 절차의 카테고리 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<ChecklistCategoryRes>> getCategories() {
		return ApiResponse.success(
				SuccessCode.OK,
				checklistService.getCategories()
		);
	}

	@Operation(summary = "카테고리별 체크리스트 조회", description = "각 카테고리의 체크리스트 목록을 조회합니다.")
	@GetMapping("/categories/{categoryId}/procedures")
	public ResponseEntity<ApiResponse<ChecklistCategoryProcedureRes>> getCategoryProcedures(
			@PathVariable Long categoryId,
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {

		log.debug("ChecklistController.getCategoryProcedures, {}", principal);

		return ApiResponse.success(
				SuccessCode.OK,
				checklistService.getCategoryProcedures(
						categoryId,
						principal.getUserId()
				)
		);
	}

	//v2
	@GetMapping("/procedures/{procedureId}")
	@Operation(
			summary = "카테고리별 체크리스트 상세 정보 조회",
			description = "각 카테고리의 체크리스트 상세 정보를 조회합니다."
	)
	public ResponseEntity<ApiResponse<ChecklistProcedureDetailRes>> getProcedureDetail(
			@PathVariable Long procedureId,
			@AuthenticationPrincipal(expression = "userId") Long userId
	) {

		return ApiResponse.success(
				SuccessCode.OK,
				checklistService.getProcedureDetail(procedureId, userId)
		);
	}

	@GetMapping("/progress")
	@Operation(summary = "전체 진행률 조회", description = "전체 진행률 및 카테고리별 진행률을 조회합니다.")
	public ResponseEntity<ApiResponse<ChecklistOverallProgressRes>> getOverallProgress(
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		return ApiResponse.success(
				SuccessCode.OK,
				checklistService.getOverallProgress(principal.getUserId())
		);
	}



}