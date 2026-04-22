package org.accompany.backend.domain.user.dto.response;

import org.accompany.backend.domain.user.entity.Provider;

import java.time.LocalDate;

public record UserProfileRes(
        String name,
        String email,
        Provider provider,
        LocalDate dateOfDeath
) {
}
