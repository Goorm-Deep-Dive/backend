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

}