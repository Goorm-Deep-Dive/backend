package org.accompany.backend.domain.survey.dto.request;

import java.util.List;

public record SurveyTempSaveReq (
        List<SurveyAnswerIdReq> answers
){
}
