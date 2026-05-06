package org.accompany.backend.domain.survey.dto.response;

import org.accompany.backend.domain.survey.entity.SurveyAnswerType;

public record SurveyAnswerRes(
        Long surveyAnswerId,
        String surveyAnswerText,
        Long nextQuestionId,
        SurveyAnswerType surveyAnswerType
) {}
