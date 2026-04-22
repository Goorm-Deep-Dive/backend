package org.accompany.backend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.user.service.UserAuthService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.dto.TokenRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserAuth API", description = "사용자 인증 API")
@RestController
@RequestMapping("/api/v1/auth/oauth2")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/refresh")
    @Operation(summary = "AccessToken 재발급", security = {}, description = "RefreshToken을 검증하여 AccessToken을 재발급합니다.")
    public ResponseEntity<ApiResponse<TokenRes>> refresh(HttpServletRequest request,
                                                        HttpServletResponse response) {
        return ApiResponse.success(SuccessCode.OK, userAuthService.refresh(request, response));
    }
}
