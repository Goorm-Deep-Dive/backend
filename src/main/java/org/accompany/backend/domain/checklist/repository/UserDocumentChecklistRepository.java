package org.accompany.backend.domain.checklist.repository;

import org.accompany.backend.domain.checklist.entity.UserDocumentChecklist;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDocumentChecklistRepository extends JpaRepository<UserDocumentChecklist, Long> {

	Optional<UserDocumentChecklist> findByUserDocumentChecklistId(Long userDocumentChecklistId);
	Optional<UserDocumentChecklist> findByProcedureDocumentProcedureDocumentIdAndDeceasedProfileDeceasedProfileId(Long procedureDocumentId, Long profileId);

	@Modifying
	@Query("delete from UserDocumentChecklist udc where udc.deceasedProfile = :deceasedProfile")
	void deleteAllByDeceasedProfile(@Param("deceasedProfile") DeceasedProfile deceasedProfile);
}
