package org.accompany.backend.domain.checklist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_procedure_checklists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProcedureChecklist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProcedureChecklistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deceased_profile_id", nullable = false)
    private DeceasedProfile deceasedProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @Column(nullable = false)
    private boolean isCheck = false;

    private LocalDateTime dueDate;


    @Builder
    public UserProcedureChecklist(
            DeceasedProfile deceasedProfile,
            Procedure procedure,
            boolean isCheck,
            LocalDateTime dueDate
    ) {
        this.deceasedProfile = deceasedProfile;
        this.procedure = procedure;
        this.isCheck = isCheck;
        this.dueDate = dueDate;
    }


    public void updateCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public void updateDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
