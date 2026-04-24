package org.accompany.backend.domain.survey.dto.response;

public record SurveyAnswerRes(
        Long surveyAnswerId,
        String surveyAnswerText,
        Long nextQuestionId
) {}
