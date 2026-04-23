package org.accompany.backend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.user.dto.response.UserProfileRes;
import org.accompany.backend.domain.user.service.UserService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "사용자 정보 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me/profile")
    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 이름, 이메일, provider, 고인 사망일자를 조회합니다.")
    public ResponseEntity<ApiResponse<UserProfileRes>> getMyProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                userService.getMyProfile(principal.getUserId())
        );
    }

//    @DeleteMapping("/me")
//    @Operation(summary = "소셜로그인 회원 탈퇴", description = "서비스 회원 탈퇴와 소셜로그인 연동을 끊습니다.")
//    public ResponseEntity<ApiResponse<Void>> withdraw(
//            @AuthenticationPrincipal CustomUserPrincipal principal,
//            HttpServletResponse response
//    ) {
//        userService.withdraw(principal.getUserId(), response);
//        return ApiResponse.success(SuccessCode.OK);
//    }
}
