package org.accompany.backend.domain.survey.service;

import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.survey.dto.response.SurveyListRes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SurveyService {
    SurveyListRes getSurveyList();
    void skipSurvey(Long userId);
    LocalDateTime calculateDueDate(Procedure procedure, LocalDate dateOfDeath);
}
