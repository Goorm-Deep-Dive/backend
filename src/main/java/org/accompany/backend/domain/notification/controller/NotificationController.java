package org.accompany.backend.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.notification.dto.response.NotificationListRes;
import org.accompany.backend.domain.notification.dto.response.NotificationTestRes;
import org.accompany.backend.domain.notification.service.NotificationService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification API", description = "알림 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "로그인한 사용자의 알림 목록을 최신순으로 조회합니다.")
    public ResponseEntity<ApiResponse<NotificationListRes>> getNotifications(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                notificationService.getNotifications(principal.getUserId())
        );
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(notificationId, principal.getUserId());
        return ApiResponse.success(SuccessCode.OK);
    }

    @PostMapping("/test")
    @Operation(summary = "FCM 테스트 발송", description = "로그인한 사용자의 모든 고인 프로필에 대해 테스트 푸시를 발송합니다. (운영 환경에서는 비활성화 권장)")
    public ResponseEntity<ApiResponse<NotificationTestRes>> sendTestNotification(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                notificationService.sendTestNotification(principal.getUserId())
        );
    }
}
