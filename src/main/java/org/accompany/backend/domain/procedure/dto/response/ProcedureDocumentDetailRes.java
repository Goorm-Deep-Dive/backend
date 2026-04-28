package org.accompany.backend.domain.procedure.dto.response;

import org.accompany.backend.domain.procedure.entity.DocumentChannelType;
import org.accompany.backend.domain.procedure.entity.DocumentType;

public record ProcedureDocumentDetailRes(
		Long procedureDocumentId,
		Long userDocumentChecklistId,

		DocumentType documentType,
		DocumentChannelType documentChannelType,

		String documentName,
		String documentLocation,
		String documentLink,

		String description,

		boolean checked
) {}
