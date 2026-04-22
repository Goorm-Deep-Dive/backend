package org.accompany.backend.domain.procedure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.survey.entity.SurveyAnswer;

@Entity
@Table(
        name = "survey_answer_procedures",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_survey_answer_procedures",
                        columnNames = {"survey_answer_id", "procedure_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyAnswerProcedure extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyAnswerProcedureId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "survey_answer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_survey_answer_procedures_answer")
    )
    private SurveyAnswer surveyAnswer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "procedure_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_survey_answer_procedures_procedure")
    )
    private Procedure procedure;

    @Builder
    private SurveyAnswerProcedure(SurveyAnswer surveyAnswer, Procedure procedure) {
        this.surveyAnswer = surveyAnswer;
        this.procedure = procedure;
    }

    public static SurveyAnswerProcedure of(SurveyAnswer surveyAnswer, Procedure procedure) {
        return SurveyAnswerProcedure.builder()
                .surveyAnswer(surveyAnswer)
                .procedure(procedure)
                .build();
    }
}
