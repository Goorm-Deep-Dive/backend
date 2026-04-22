package org.accompany.backend.domain.survey.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.procedure.entity.SurveyAnswerProcedure;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question_id", nullable = false)
    private SurveyQuestion surveyQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_question_id")
    private SurveyQuestion nextQuestion;

    @Column(nullable = false, length = 200)
    private String surveyAnswerText;

    @OneToMany(mappedBy = "surveyAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyAnswerProcedure> surveyAnswerProcedures = new ArrayList<>();

    @Builder
    private SurveyAnswer(
            SurveyQuestion surveyQuestion,
            SurveyQuestion nextQuestion,
            String surveyAnswerText
    ) {
        this.surveyQuestion = surveyQuestion;
        this.nextQuestion = nextQuestion;
        this.surveyAnswerText = surveyAnswerText;
    }
}