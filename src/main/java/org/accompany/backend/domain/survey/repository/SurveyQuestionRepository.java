package org.accompany.backend.domain.survey.repository;

import org.accompany.backend.domain.survey.entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    List<SurveyQuestion> findAllByOrderBySurveyQuestionIdAsc();
}
