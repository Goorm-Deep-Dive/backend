package org.accompany.backend.domain.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyRequirementType {

    REQUIRED("필수"),
    OPTIONAL("선택");

    private final String label;
}
