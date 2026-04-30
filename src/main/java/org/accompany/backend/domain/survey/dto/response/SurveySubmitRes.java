package org.accompany.backend.domain.survey.dto.response;

public record SurveySubmitRes (
    String surveyStatus,
    int procedureChecklistCount,
    int documentChecklistCount
    ){}
