package org.accompany.backend.domain.checklist.dto.response;

import org.accompany.backend.domain.procedure.dto.response.ProcedureDocumentDetailRes;

import java.util.List;

public record ChecklistProcedureDetailRes(

		Long userProcedureChecklistId,
		Long procedureId,
		Long procedureCategoryId,

		String procedureName,
		String description,//260428 수정사항 (추가)

		String dueDateDescription,
		String searchScope,
		String cautionText,

		List<Channel> channels,
		List<Contact> contacts,
		List<Document> documents,

//		List<ProcedureDocumentDetailRes> documents, //*****서류별 상세 조회 합치는 버전

		boolean checked
) {

	public record Channel(
			Long procedureChannelId,
			String channelType,
			String description
	) {}

	public record Contact(
			Long procedureContactId,
			String title,
			String description
	) {}

	public record Document(
			Long procedureDocumentId, //260428 수정사항 (추가)
			Long userDocumentChecklistId,
			String documentName,
			boolean checked
	) {}
}