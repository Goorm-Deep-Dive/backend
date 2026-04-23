package org.accompany.backend.domain.checklist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.procedure.entity.ProcedureDocument;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;

@Entity
@Table(name = "user_document_checklists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDocumentChecklist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDocumentChecklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deceased_profile_id", nullable = false)
    private DeceasedProfile deceasedProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_document_id", nullable = false)
    private ProcedureDocument procedureDocument;

    @Column(nullable = false)
    private boolean isChecked = false;

    @Builder
    public UserDocumentChecklist(
            DeceasedProfile deceasedProfile,
            ProcedureDocument procedureDocument,
            boolean isChecked
    ) {
        this.deceasedProfile = deceasedProfile;
        this.procedureDocument = procedureDocument;
        this.isChecked = isChecked;
    }


    public void updateChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}