package org.accompany.backend.domain.procedure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "procedure_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcedureDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long procedureDocumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentType documentType;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentChannelType documentChannelType;

    @Column(nullable = false, length = 200)
    private String documentName;

    @Column(length = 255)
    private String documentLocation;

    @Column(length = 255)
    private String documentLink;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    private ProcedureDocument(
            Procedure procedure,
            DocumentType documentType,
            DocumentChannelType documentChannelType,
            String documentName,
            String documentLocation,
            String documentLink,
            String description
    ) {
        this.procedure = procedure;
        this.documentType = documentType;
        this.documentChannelType = documentChannelType;
        this.documentName = documentName;
        this.documentLocation = documentLocation;
        this.documentLink = documentLink;
        this.description = description;
    }
}
