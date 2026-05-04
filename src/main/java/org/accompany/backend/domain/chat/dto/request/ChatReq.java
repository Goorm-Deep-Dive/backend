package org.accompany.backend.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatReq(
        @NotBlank(message = "메시지는 필수입니다.")
        String message
) {
        public ChatReq {
                // 제어 문자 제거
                if (message != null) {
                        message = message.replaceAll("[\\x00-\\x1F\\x7F]", "");
                }
        }
}
