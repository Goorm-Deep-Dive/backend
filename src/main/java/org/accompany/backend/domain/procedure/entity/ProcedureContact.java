package org.accompany.backend.domain.procedure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;

@Entity
@Table(name = "procedure_contacts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcedureContact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long procedureContactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @Column(length = 100)
    private String title;

    @Column(length = 255)
    private String description;

    @Builder
    private ProcedureContact(
            Procedure procedure,
            String title,
            String description
    ) {
        this.procedure = procedure;
        this.title = title;
        this.description = description;
    }
}
