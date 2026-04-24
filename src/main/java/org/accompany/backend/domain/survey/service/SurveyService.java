package org.accompany.backend.domain.survey.service;

import org.accompany.backend.domain.survey.dto.response.SurveyListRes;

public interface SurveyService {
    SurveyListRes getSurveyList();
    void skipSurvey(Long userId);
}
