package org.accompany.backend.domain.checklist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProcedureRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryRes;
import org.accompany.backend.domain.checklist.service.ChecklistService;
import org.accompany.backend.domain.user.service.UserService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Checklist API", description = "체크리스트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checklists")
public class ChecklistController {

	private final ChecklistService checklistService;
	private final UserService userService;

	@SecurityRequirement(name = "BearerAuth")
	@GetMapping("/categories")
	@Operation(summary = "카테고리 목록 조회", description = "체크리스트의 카테고리 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<ChecklistCategoryRes>> getCategories() {
		return ApiResponse.success(
				SuccessCode.OK,
				checklistService.getCategories()
		);
	}

//
//	/*
//	유저가 고인의 프로필을 선택할 수 있어야한다.
//
//	 */
//	@GetMapping("/categories/{categoryId}/procedures")
//	@Operation(summary = "각 카테고리 별 체크리스트 목록 조회", description = "각 카테고리 별 체크리스트의 목록을 조회합니다.")
//	public ResponseEntity<ApiResponse<ChecklistCategoryProcedureRes>> getCategoryProcedures(
//			@PathVariable Long categoryId,
//			@RequestParam Long profileId,
//			@AuthenticationPrincipal CustomUserPrincipal principal
//	) {
//		return ApiResponse.success(
//				SuccessCode.OK,
//				checklistService.getCategoryProcedures(
//						categoryId,
//						profileId,
//						principal.getUserId()
//				)
//		);
//	}
}