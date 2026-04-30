package org.accompany.backend.domain.checklist.service;

import org.accompany.backend.domain.checklist.dto.response.*;

import java.util.List;

public interface ChecklistService {

	ChecklistCategoryRes getCategories();
	ChecklistCategoryProcedureRes getCategoryProcedures(Long categoryId, Long userId);
	ChecklistProcedureDetailRes getProcedureDetail(Long procedureId, Long userId);
	ChecklistOverallProgressRes getOverallProgress(Long userId);
	void modifyProcedureCheck(Long userProcedureChecklistId, Long userId, boolean isChecked);
	void modifyDocumentCheck(Long procedureDocumentId, Long userId, boolean isChecked);
	List<OptionalProcedureRes> getOptionalProcedures(Long userId);
	void createOptionalProcedure(Long procedureId, Long userId);
	void deleteProcedureChecklist(Long userProcedureChecklistId, Long userId);
}