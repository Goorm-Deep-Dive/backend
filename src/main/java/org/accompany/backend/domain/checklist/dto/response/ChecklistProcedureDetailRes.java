package org.accompany.backend.domain.checklist.dto.response;

import java.util.List;

public record ChecklistProcedureDetailRes(

		Long userProcedureChecklistId,
		Long procedureId,
		Long procedureCategoryId,

		String procedureName,

		String dueDateDescription,
		String searchScope,
		String cautionText,

		List<Channel> channels,
		List<Contact> contacts,
		List<Document> documents,

		Boolean isCheck
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
			Long userDocumentChecklistId,
			String documentName,
			Boolean isChecked
	) {}
}