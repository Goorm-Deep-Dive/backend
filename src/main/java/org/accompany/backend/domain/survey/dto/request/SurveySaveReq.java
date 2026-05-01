package org.accompany.backend.domain.survey.dto.request;

import java.util.List;

public record SurveySaveReq (
        List<SurveyAnswerIdReq> answers
){
}
