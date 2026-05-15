package org.accompany.backend.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenReq(
        @NotBlank(message = "FCM 토큰은 필수입니다.")
        String fcmToken
) {
}
