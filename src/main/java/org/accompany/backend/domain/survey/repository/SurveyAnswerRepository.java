package org.accompany.backend.domain.survey.repository;

import org.accompany.backend.domain.survey.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

    @Query("select distinct a from SurveyAnswer a " +
            "left join fetch a.surveyAnswerProcedures " +
            "left join fetch a.surveyQuestion " +
            "where a.surveyAnswerId in :ids " +
            "order by a.surveyQuestion.surveyQuestionId, a.surveyAnswerId")
    List<SurveyAnswer> findAllWithProceduresAndQuestionByIds(@Param("ids")List<Long> ids);

    @Query("select distinct a from SurveyAnswer a "+
            "left join fetch a.surveyAnswerProcedures " +
            "where a.surveyQuestion.surveyQuestionId in :questionIds ")
    List<SurveyAnswer> findAllWithProceduresByQuestionIds(@Param("questionIds")Set<Long> questionIds);
}
