package org.accompany.backend.domain.deceasedProfile.dto.request;

import java.time.LocalDate;

public record DeceasedProfileUpdateReq(

        String name,
        LocalDate dateOfDeath

) {}
