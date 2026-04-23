package org.accompany.backend.domain.deceasedProfile.dto.response;

import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;

import java.time.LocalDate;

public record DeceasedProfileRes(
        Long deceasedProfileId,
        String name,
        LocalDate dateOfDeath
) {
    public static DeceasedProfileRes from(DeceasedProfile deceasedProfile) {
        return new DeceasedProfileRes(
                deceasedProfile.getDeceasedProfileId(),
                deceasedProfile.getName(),
                deceasedProfile.getDateOfDeath()
        );
    }
}
