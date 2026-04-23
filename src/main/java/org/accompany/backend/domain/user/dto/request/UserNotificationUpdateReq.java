package org.accompany.backend.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserNotificationUpdateReq(
        @NotNull(message = "알림 설정 여부는 필수입니다.")
        Boolean notificationEnabled
) {
}
