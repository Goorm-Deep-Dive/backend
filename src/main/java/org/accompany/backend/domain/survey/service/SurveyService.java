package org.accompany.backend.domain.survey.service;

import org.accompany.backend.domain.survey.dto.response.SurveyListRes;
import org.accompany.backend.domain.survey.dto.request.SurveyTempSaveReq;
import org.accompany.backend.domain.survey.dto.response.SurveyTempSaveRes;

public interface SurveyService {
    SurveyListRes getSurveyList();
    void skipSurvey(Long userId);
    SurveyTempSaveRes saveTempSurvey(Long userId, SurveyTempSaveReq request);
}
