package org.accompany.backend.domain.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.survey.dto.request.SurveyTempSaveReq;
import org.accompany.backend.domain.survey.dto.response.SurveyListRes;
import org.accompany.backend.domain.survey.dto.response.SurveyTempSaveRes;
import org.accompany.backend.domain.survey.service.SurveyService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Survey API", description = "설문조사 API")
@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping
    @Operation(summary = "설문조사 목록 조회", description = "전체 설문 질문과 답변 선택지를 조회합니다.")
    public ResponseEntity<ApiResponse<SurveyListRes>> getSurveyList() {
        return ApiResponse.success(SuccessCode.OK, surveyService.getSurveyList());
    }

    @PostMapping("/skip")
    @Operation(summary = "설문조사 스킵", description = "설문조사를 건너뛰고 전체 체크리스트를 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> skipSurvey(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        surveyService.skipSurvey(principal.getUserId());
        return ApiResponse.success(SuccessCode.OK);
    }

    @PostMapping("/temp")
    @Operation(summary = "설문조사 임시저장", description = "설문조사를 임시 저장합니다.")
    public ResponseEntity<ApiResponse<SurveyTempSaveRes>> saveTempSurvey(
            @AuthenticationPrincipal
            CustomUserPrincipal principal,
            @RequestBody
            SurveyTempSaveReq request
    ) {
        return ApiResponse.success(SuccessCode.OK, surveyService.saveTempSurvey(principal.getUserId(), request));
    }
}
