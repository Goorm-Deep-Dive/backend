package org.accompany.backend.domain.checklist.service;

import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProcedureRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistOverallProgressRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistProcedureDetailRes;

public interface ChecklistService {

	ChecklistCategoryRes getCategories();
	ChecklistCategoryProcedureRes getCategoryProcedures(Long categoryId, Long userId);
	ChecklistProcedureDetailRes getProcedureDetail(Long procedureId, Long userId);
	ChecklistOverallProgressRes getOverallProgress(Long userId);
	void modifyProcedureCheck(Long checklistId, Long userId, boolean isChecked);
	void modifyDocumentCheck(Long procedureDocumentId, Long userId, boolean isChecked);
}