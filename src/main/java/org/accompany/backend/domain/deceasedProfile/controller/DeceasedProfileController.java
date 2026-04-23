package org.accompany.backend.domain.deceasedProfile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileCreateReq;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileUpdateReq;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileListRes;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileRes;
import org.accompany.backend.domain.deceasedProfile.service.DeceasedProfileService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "DeceasedProfile API", description = "고인 정보 API")
@RestController
@RequestMapping("/api/v1/deceased-profile")
@RequiredArgsConstructor
public class DeceasedProfileController {

    private final DeceasedProfileService deceasedProfileService;

    @PostMapping
    @Operation(summary = "고인 정보 생성", description = "로그인한 사용자의 고인 정보를 생성합니다.")
    public ResponseEntity<ApiResponse<DeceasedProfileRes>> createDeceasedProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody DeceasedProfileCreateReq request
    ) {
        DeceasedProfileRes deceasedProfileRes = deceasedProfileService.createDeceasedProfile(principal.getUserId(), request);

        return ApiResponse.success(
                SuccessCode.CREATED,
                deceasedProfileRes
        );
    }

    @GetMapping
    @Operation(summary = "고인 목록 조회", description = "로그인한 사용자의 고인 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<DeceasedProfileListRes>>> getDeceasedProfiles(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                deceasedProfileService.getDeceasedProfiles(principal.getUserId())
        );
    }

    @GetMapping("/active")
    @Operation(summary = "현재 고인 정보 조회", description = "로그인한 사용자의 현재 고인 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<DeceasedProfileRes>> getActiveDeceasedProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                deceasedProfileService.getActiveDeceasedProfile(principal.getUserId())
        );
    }

    @PatchMapping("/{deceasedProfileId}")
    @Operation(summary = "고인 정보 수정", description = "로그인한 사용자의 고인 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> modifyDeceasedProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long deceasedProfileId,
            @Valid @RequestBody DeceasedProfileUpdateReq request
    ) {
        deceasedProfileService.modifyDeceasedProfile(principal.getUserId(), deceasedProfileId, request);

        return ApiResponse.success(SuccessCode.OK);
    }

    @PatchMapping("/{deceasedProfileId}/active")
    @Operation(summary = "현재 고인 정보 변경", description = "로그인한 사용자의 현재 고인 정보를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> changeActiveDeceasedProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long deceasedProfileId
    ) {
        deceasedProfileService.changeActiveDeceasedProfile(principal.getUserId(), deceasedProfileId);

        return ApiResponse.success(SuccessCode.OK);
    }
}
