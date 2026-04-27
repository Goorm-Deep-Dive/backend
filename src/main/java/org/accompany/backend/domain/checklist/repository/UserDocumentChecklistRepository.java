package org.accompany.backend.domain.checklist.repository;

import org.accompany.backend.domain.checklist.entity.UserDocumentChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDocumentChecklistRepository extends JpaRepository<UserDocumentChecklist, Long> {

	Optional<UserDocumentChecklist> findByUserDocumentChecklistId(Long userDocumentChecklistId);
	Optional<UserDocumentChecklist> findByProcedureDocumentProcedureDocumentIdAndDeceasedProfileDeceasedProfileId(Long procedureDocumentId, Long profileId);

}
