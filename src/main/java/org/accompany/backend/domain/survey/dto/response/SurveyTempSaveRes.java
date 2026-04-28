package org.accompany.backend.domain.survey.dto.response;

import java.util.List;

public record SurveyTempSaveRes (
        String surveyStatus,
        List<SurveyResponseRes> answers
){}
