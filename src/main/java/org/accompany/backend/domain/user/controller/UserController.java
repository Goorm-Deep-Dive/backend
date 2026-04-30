package org.accompany.backend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.user.dto.request.UserNotificationUpdateReq;
import org.accompany.backend.domain.user.dto.response.UserProfileRes;
import org.accompany.backend.domain.user.service.UserService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Tag(name = "User API", description = "사용자 정보 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final String GOOGLE_LINK_AUTHORIZATION_PATH = "/api/v1/auth/oauth2/authorization/google";

    private final UserService userService;

    @GetMapping("/me/profile")
    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserProfileRes>> getMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                userService.getMyProfile(principal.getUserId())
        );
    }

    @PatchMapping("/me/notification")
    @Operation(summary = "내 알림 설정 변경", description = "로그인한 사용자의 알림 설정 여부를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updateNotification(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UserNotificationUpdateReq request
    ) {
        userService.updateNotification(principal.getUserId(), request.notificationEnabled());

        return ApiResponse.success(SuccessCode.OK);
    }

    @GetMapping("/me/google/link")
    @Operation(summary = "구글 연동 시작", description = "로그인한 사용자의 구글 계정 연동을 시작합니다.(Swagger에서 테스트 불가)")
    public void linkGoogle(
            @RequestParam("redirect_uri") String redirectUri,
            HttpServletResponse response
    ) throws IOException {
        String linkUrl = UriComponentsBuilder.fromPath(GOOGLE_LINK_AUTHORIZATION_PATH)
                .queryParam("link_google", true)
                .queryParam("redirect_uri", redirectUri)
                .build(true)
                .toUriString();

        response.sendRedirect(linkUrl);
    }

    @DeleteMapping("/me")
    @Operation(summary = "소셜로그인 회원 탈퇴", description = "서비스 회원 탈퇴와 소셜로그인 연동을 끊습니다.")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletResponse response
    ) {
        userService.withdraw(principal.getUserId(), response);
        return ApiResponse.success(SuccessCode.OK);
    }
}
