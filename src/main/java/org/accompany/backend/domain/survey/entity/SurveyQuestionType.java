package org.accompany.backend.domain.survey.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurveyQuestionType {

    SINGLE("단일 선택"),
    MULTIPLE("복수 선택");

    private final String label;
}
