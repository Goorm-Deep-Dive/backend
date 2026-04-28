package org.accompany.backend.domain.procedure.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.procedure.dto.response.ProcedureDocumentDetailRes;
import org.accompany.backend.domain.procedure.service.ProcedureService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Procedure API", description = "procedure API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/procedures")
public class ProcedureController {

	private final ProcedureService procedureService;

	@GetMapping("/documents/{procedureDocumentId}")
	@Operation(summary = "서류별 상세 조회", description = "서류별 상세 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<ProcedureDocumentDetailRes>> getProcedureDocumentDetail(
			@PathVariable Long procedureDocumentId,
			@AuthenticationPrincipal CustomUserPrincipal principal
	) {
		return ApiResponse.success(
				SuccessCode.OK,
				procedureService.getProcedureDocumentDetail(
						procedureDocumentId,
						principal.getUserId()
				)
		);
	}
}
