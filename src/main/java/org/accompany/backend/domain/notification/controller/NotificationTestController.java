package org.accompany.backend.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.notification.dto.response.NotificationTestRes;
import org.accompany.backend.domain.notification.service.NotificationService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification Test API", description = "FCM 발송 테스트 API (운영 환경 비활성화 가능)")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.test-enabled", havingValue = "true")
public class NotificationTestController {

    private final NotificationService notificationService;

    @PostMapping("/test")
    @Operation(summary = "FCM 테스트 발송", description = "로그인한 사용자의 모든 고인 프로필에 대해 테스트 푸시를 발송합니다.")
    public ResponseEntity<ApiResponse<NotificationTestRes>> sendTestNotification(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                notificationService.sendTestNotification(principal.getUserId())
        );
    }
}
