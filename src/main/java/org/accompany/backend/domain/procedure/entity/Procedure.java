package org.accompany.backend.domain.procedure.entity;


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
@Table(name = "procedures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Procedure extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long procedureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_category_id", nullable = false)
    private ProcedureCategory procedureCategory;

    @Column(nullable = false, length = 100)
    private String procedureName;

    @Column(length = 255)
    private String description;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DueDateType dueDateType;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DueDateUnit dueDateUnit;

    private Integer dueDate;

    @Column(length = 200)
    private String dueDateDescription;

    @Column(length = 500)
    private String searchScope;

    @Column(columnDefinition = "TEXT")
    private String cautionText;

    @OneToMany(mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyAnswerProcedure> surveyAnswerProcedures = new ArrayList<>();

    @OneToMany(mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcedureDocument> procedureDocuments = new ArrayList<>();

    @OneToMany(mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcedureChannel> procedureChannels = new ArrayList<>();

    @OneToMany(mappedBy = "procedure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcedureContact> procedureContacts = new ArrayList<>();

    @Builder
    private Procedure(
            ProcedureCategory procedureCategory,
            String procedureName,
            String description,
            DueDateType dueDateType,
            DueDateUnit dueDateUnit,
            Integer dueDate,
            String dueDateDescription,
            String searchScope,
            String cautionText
    ) {
        this.procedureCategory = procedureCategory;
        this.procedureName = procedureName;
        this.description = description;
        this.dueDateType = dueDateType;
        this.dueDateUnit = dueDateUnit;
        this.dueDate = dueDate;
        this.dueDateDescription = dueDateDescription;
        this.searchScope = searchScope;
        this.cautionText = cautionText;
    }
}