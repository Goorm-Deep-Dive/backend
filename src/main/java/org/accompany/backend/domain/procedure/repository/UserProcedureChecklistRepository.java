package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProcedureChecklistRepository extends JpaRepository<UserProcedureChecklist, Long> {
}