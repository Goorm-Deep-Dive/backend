package org.accompany.backend.domain.survey.dto.response;

import java.util.List;

public record SurveyListRes(
        List<SurveyQuestionRes> surveys
) {}
