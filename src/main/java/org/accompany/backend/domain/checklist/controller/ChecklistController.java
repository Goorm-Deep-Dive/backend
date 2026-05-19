package org.accompany.backend.domain.checklist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.dto.request.ChecklistCheckReq;
import org.accompany.backend.domain.checklist.dto.response.*;
import org.accompany.backend.domain.checklist.service.ChecklistService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Checklist API", description = "체크리스트 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checklists")
public class ChecklistController {

	private final ChecklistService checklistService;

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

		return ApiResponse.success(SuccessCode.OK, checklistService.getCategoryProcedures(categoryId, principal.getUserId()));
	}

	@GetMapping("/procedures/{procedureId}")
	@Operation(summary = "카테고리별 체크리스트 상세 정보 조회", description = "각 카테고리의 체크리스트 상세 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<ChecklistProcedureDetailRes>> getProcedureDetail(
			@PathVariable Long procedureId,
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {

		return ApiResponse.success(
				SuccessCode.OK,
				checklistService.getProcedureDetail(procedureId, principal.getUserId())
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

	@Operation(summary = "과업 체크리스트 is_checked 상태 변경", description = "각 절차의 체크리스트 상태를 변경합니다.")
	@PatchMapping("/procedures/{userProcedureChecklistId}")
	public ResponseEntity<ApiResponse<Void>> modifyProcedureCheck(
			@PathVariable Long userProcedureChecklistId,
			@RequestBody ChecklistCheckReq req,
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {

		checklistService.modifyProcedureCheck(
				userProcedureChecklistId,
				principal.getUserId(),
				req.checked()
		);

		return ApiResponse.success(SuccessCode.USER_PROCEDURE_CHECKLIST_UPDATED);
	}

	@Operation(summary = "문서 체크리스트 is_checked 상태 변경", description = "각 문서의 체크리스트 상태를 변경합니다.")
	@PatchMapping("/documents/{procedureDocumentId}")
	public ResponseEntity<ApiResponse<Void>> modifyDocumentCheck(
			@PathVariable Long procedureDocumentId,
			@RequestBody ChecklistCheckReq req,
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {

		checklistService.modifyDocumentCheck(
				procedureDocumentId,
				principal.getUserId(),
				req.checked()
		);

		return ApiResponse.success(SuccessCode.USER_DOCUMENT_CHECKLIST_UPDATED);
	}


	@Operation(summary = "추가 가능 체크리스트 목록 조회", description = "사용자가 추가 가능한 선택(가변) 체크리스트 항목을 조회합니다.")
	@GetMapping("/procedures/available")
	public ResponseEntity<ApiResponse<List<OptionalProcedureRes>>> getOptionalProcedures(
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		return ApiResponse.success(
				SuccessCode.OK,
				checklistService.getOptionalProcedures(principal.getUserId())
		);
	}

	/**
	 * 선택 체크리스트 추가 (+ 버튼)
	 */
	@Operation(summary = "선택 체크리스트 항목 추가", description = "사용자가 선택한 선택(가변) 체크리스트 항목을 추가합니다.")
	@PostMapping("/procedures/{procedureId}")
	public ResponseEntity<ApiResponse<Void>> createOptionalProcedure(
			@PathVariable Long procedureId,
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		checklistService.createOptionalProcedure(procedureId, principal.getUserId());

		return ApiResponse.success(
				SuccessCode.USER_PROCEDURE_CHECKLIST_CREATED
		);
	}


	/**
	 * 선택 체크리스트 삭제 (- 버튼)
	 */
	@Operation(summary = "선택 체크리스트 항목 삭제", description = "사용자가 선택(가변) 체크리스트 항목을 삭제합니다.")
	@DeleteMapping("/procedures/{userProcedureChecklistId}")
	public ResponseEntity<ApiResponse<Void>> deleteProcedureChecklist(
			@PathVariable Long userProcedureChecklistId,
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		checklistService.deleteProcedureChecklist(userProcedureChecklistId, principal.getUserId());

		return ApiResponse.success(
				SuccessCode.USER_PROCEDURE_CHECKLIST_DELETED
		);
	}


}