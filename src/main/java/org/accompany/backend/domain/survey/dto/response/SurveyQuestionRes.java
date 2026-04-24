package org.accompany.backend.domain.survey.dto.response;

import java.util.List;

public record SurveyQuestionRes(
        Long surveyQuestionId,
        String surveyQuestionText,
        String questionType,
        String requirementType,
        String description,
        List<SurveyAnswerRes> answers
) {}
