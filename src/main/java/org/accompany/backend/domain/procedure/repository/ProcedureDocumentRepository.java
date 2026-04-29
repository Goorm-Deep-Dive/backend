package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.procedure.entity.ProcedureDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcedureDocumentRepository
		extends JpaRepository<ProcedureDocument, Long> {
}