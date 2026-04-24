package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProgressRes;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProcedureChecklistRepository extends JpaRepository<UserProcedureChecklist, Long> {

    @Query("""
        select new org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProgressRes(
            pc.procedureCategoryId,
            pc.categoryName,
            0,
            cast(count(upc) as integer),
            cast(sum(case when upc.isCheck = true then 1 else 0 end) as integer)
        )
        from UserProcedureChecklist upc
        join upc.procedure p
        join p.procedureCategory pc
        where upc.deceasedProfile.deceasedProfileId = :profileId
        group by pc.procedureCategoryId, pc.categoryName
        order by pc.procedureCategoryId asc
    """)
    List<ChecklistCategoryProgressRes> findCategoryProgresses(@Param("profileId") Long profileId);

    Optional<UserProcedureChecklist> findByProcedureProcedureIdAndDeceasedProfileDeceasedProfileId(Long procedureId, Long profileId);

}