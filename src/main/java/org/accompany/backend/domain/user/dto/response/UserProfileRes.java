package org.accompany.backend.domain.user.dto.response;

import org.accompany.backend.domain.user.entity.Provider;

public record UserProfileRes(
        String name,
        String email,
        Provider provider,
        boolean notificationEnabled,
        boolean googleLinked
) {
}
