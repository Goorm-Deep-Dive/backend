package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.procedure.entity.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {
}
