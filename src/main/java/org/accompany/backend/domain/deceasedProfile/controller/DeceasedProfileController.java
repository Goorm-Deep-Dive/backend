package org.accompany.backend.domain.deceasedProfile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileCreateReq;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileRes;
import org.accompany.backend.domain.deceasedProfile.service.DeceasedProfileService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DeceasedProfile API", description = "고인 프로필 API")
@RestController
@RequestMapping("/api/v1/deceased-profile")
@RequiredArgsConstructor
public class DeceasedProfileController {

    private final DeceasedProfileService deceasedProfileService;

    @PostMapping
    @Operation(summary = "고인 프로필 생성", description = "로그인한 사용자의 고인 프로필을 생성합니다.")
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
}
