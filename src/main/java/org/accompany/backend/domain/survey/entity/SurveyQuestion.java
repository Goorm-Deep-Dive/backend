package org.accompany.backend.domain.survey.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyQuestionId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurveyQuestionType questionType;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurveyRequirementType requirementType;

    @Column(nullable = false, length = 200)
    private String surveyQuestionText;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "surveyQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyAnswer> surveyAnswers = new ArrayList<>();

    @Builder
    private SurveyQuestion(
            SurveyQuestionType questionType,
            SurveyRequirementType requirementType,
            String surveyQuestionText,
            String description
    ) {
        this.questionType = questionType;
        this.requirementType = requirementType;
        this.surveyQuestionText = surveyQuestionText;
        this.description = description;
    }
}