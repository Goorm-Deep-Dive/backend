package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.checklist.dto.ProcedureChecklistQueryDto;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {
	@Query("""
    select new org.accompany.backend.domain.checklist.dto.ProcedureChecklistQueryDto(
        p.procedureId,
        p.procedureName,
        upc.userProcedureChecklistId,
        coalesce(upc.isCheck, false),
        upc.dueDate,

        p.dueDate,
        p.dueDateUnit,
        p.dueDateType
    )
    from Procedure p
    left join UserProcedureChecklist upc
        on upc.procedure = p
        and upc.deceasedProfile.deceasedProfileId = :profileId
    where p.procedureCategory.procedureCategoryId = :categoryId
    order by p.procedureId asc
""")
	List<ProcedureChecklistQueryDto> findProceduresWithChecklist(
			@Param("categoryId") Long categoryId,
			@Param("profileId") Long profileId
	);

	@Query("select distinct p from Procedure p left join fetch p.procedureDocuments")
	List<Procedure> findAllWithDocuments();

	@EntityGraph(attributePaths = {
			"procedureChannels"
	})
	Optional<Procedure> findWithDetailsByProcedureId(Long procedureId);
}


