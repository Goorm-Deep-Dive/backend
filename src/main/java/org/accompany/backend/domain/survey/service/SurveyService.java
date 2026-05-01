package org.accompany.backend.domain.survey.service;

import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.survey.dto.response.SurveyListRes;
import org.accompany.backend.domain.survey.dto.request.SurveySaveReq;
import org.accompany.backend.domain.survey.dto.response.SurveySubmitRes;
import org.accompany.backend.domain.survey.dto.response.SurveyTempSaveRes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SurveyService {
    SurveyListRes getSurveyList(Long userId);
    void skipSurvey(Long userId);
    SurveyTempSaveRes saveTempSurvey(Long userId, SurveySaveReq request);
    SurveySubmitRes submitSurvey(Long userId, SurveySaveReq request);

    LocalDateTime calculateDueDate(Procedure procedure, LocalDate dateOfDeath);
}
