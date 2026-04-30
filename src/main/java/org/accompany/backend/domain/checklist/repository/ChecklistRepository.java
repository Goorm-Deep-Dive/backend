package org.accompany.backend.domain.checklist.repository;

import org.accompany.backend.domain.checklist.dto.ProcedureChecklistQueryDto;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChecklistRepository extends JpaRepository <Procedure, Long> {


	@Query("""
    select new org.accompany.backend.domain.checklist.dto.ProcedureChecklistQueryDto(
        p.procedureId,
        p.procedureName,
        upc.userProcedureChecklistId,
        coalesce(upc.isChecked, false),
        upc.dueDate,

        p.dueDate,
        p.dueDateUnit,
        p.dueDateType,
        
        p.priority
    )
    from Procedure p
    left join UserProcedureChecklist upc
        on upc.procedure = p
        and upc.deceasedProfile.deceasedProfileId = :profileId
    where p.procedureCategory.procedureCategoryId = :categoryId
    order by p.priority asc
""")
	List<ProcedureChecklistQueryDto> findProceduresWithChecklist(
			@Param("categoryId") Long categoryId,
			@Param("profileId") Long profileId
	);

	/**
	 * priority=3 이면서
	 * 아직 현재 profile checklist 에 없는 항목 조회
	 */
	@Query("""
        select p
        from Procedure p
        join fetch p.procedureCategory c
        where p.priority = 3
        and not exists (
            select 1
            from UserProcedureChecklist u
            where u.procedure = p
            and u.deceasedProfile.deceasedProfileId = :profileId
        )
        order by c.procedureCategoryId asc, p.procedureId asc
    """)
	List<Procedure> findAvailableOptionalProcedures(Long profileId);

}
