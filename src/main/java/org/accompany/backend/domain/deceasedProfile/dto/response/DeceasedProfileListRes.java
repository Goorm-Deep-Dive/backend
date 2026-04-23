package org.accompany.backend.domain.deceasedProfile.dto.response;

import java.time.LocalDate;

public record DeceasedProfileListRes(

        Long deceasedProfileId,
        String name,
        LocalDate dateOfDeath,
        boolean active
) {
}
