package org.accompany.backend.domain.checklist.repository;

import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProcedureChecklistRepository extends JpaRepository<UserProcedureChecklist, Long> {

	Optional<UserProcedureChecklist> findByProcedureProcedureIdAndDeceasedProfileDeceasedProfileId(Long procedureId, Long profileId);

	Optional<UserProcedureChecklist> findByUserProcedureChecklistId(Long userProcedureChecklistId);

	@Query("""
		select upc
		from UserProcedureChecklist upc
		join fetch upc.procedure
		where upc.deceasedProfile.deceasedProfileId = :deceasedProfileId
	""")
	List<UserProcedureChecklist> findAllWithProcedureByDeceasedProfileId(
			@Param("deceasedProfileId") Long deceasedProfileId
	);

	boolean existsByProcedureProcedureIdAndDeceasedProfileDeceasedProfileId(
			Long procedureId,
			Long profileId
	);

	@Modifying
	@Query("delete from UserProcedureChecklist upc where upc.deceasedProfile = :deceasedProfile")
	void deleteAllByDeceasedProfile(@Param("deceasedProfile") DeceasedProfile deceasedProfile);
}