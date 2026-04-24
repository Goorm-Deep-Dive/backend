package org.accompany.backend.domain.checklist.service;

import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProcedureRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryRes;

public interface ChecklistService {

	ChecklistCategoryRes getCategories();
	ChecklistCategoryProcedureRes getCategoryProcedures(Long categoryId, Long userId);

}