package org.accompany.backend.domain.survey.dto.response;

import java.util.List;

public record SurveyListRes(
        String surveyStatus,
        List<SurveyQuestionRes> surveys,
        List<Long> selectedAnswerIds
) {}
