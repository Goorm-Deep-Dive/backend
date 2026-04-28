package org.accompany.backend.domain.survey.repository;

import org.accompany.backend.domain.survey.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
}
