package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.procedure.entity.ProcedureCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcedureCategoryRepository extends JpaRepository<ProcedureCategory, Long> {
}
