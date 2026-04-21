package org.accompany.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyStatus {

    NOT_STARTED("미시작"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료"),
    SKIPPED("건너뜀");

    private final String label;
}
