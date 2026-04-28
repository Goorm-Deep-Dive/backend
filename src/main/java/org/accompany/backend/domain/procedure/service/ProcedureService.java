package org.accompany.backend.domain.procedure.service;

import org.accompany.backend.domain.procedure.dto.response.ProcedureDocumentDetailRes;

public interface ProcedureService {
	ProcedureDocumentDetailRes getProcedureDocumentDetail(Long procedureDocumentId, Long userId);
}
