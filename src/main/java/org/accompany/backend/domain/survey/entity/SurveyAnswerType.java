package org.accompany.backend.domain.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyAnswerType {

    NORMAL("일반 답변", 0),
    UNKNOWN("잘 모름", 2),
    NOT_APPLICABLE("해당 없음", 1);

    private final String label;
    private final int order;
}
