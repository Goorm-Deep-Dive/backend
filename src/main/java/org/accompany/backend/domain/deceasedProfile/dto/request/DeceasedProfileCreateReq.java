package org.accompany.backend.domain.deceasedProfile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DeceasedProfileCreateReq(

        @NotBlank(message = "고인 이름은 필수입니다.")
        String name,

        @NotNull(message = "영면일은 필수입니다.")
        LocalDate dateOfDeath

) {}
