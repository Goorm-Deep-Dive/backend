package org.accompany.backend.domain.survey.service;

import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.survey.dto.response.SurveyListRes;
import org.accompany.backend.domain.survey.dto.request.SurveyTempSaveReq;
import org.accompany.backend.domain.survey.dto.response.SurveyTempSaveRes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SurveyService {
    SurveyListRes getSurveyList();
    void skipSurvey(Long userId);
    SurveyTempSaveRes saveTempSurvey(Long userId, SurveyTempSaveReq request);
    LocalDateTime calculateDueDate(Procedure procedure, LocalDate dateOfDeath);
}
