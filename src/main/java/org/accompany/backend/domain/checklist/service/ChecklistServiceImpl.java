package org.accompany.backend.domain.checklist.service;

import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProcedureRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryRes;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.procedure.entity.ProcedureCategory;
import org.accompany.backend.domain.procedure.repository.ProcedureCategoryRepository;
import org.accompany.backend.domain.procedure.repository.ProcedureRepository;
import org.accompany.backend.domain.deceasedProfile.repository.DeceasedProfileRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //TODO:
public class ChecklistServiceImpl implements ChecklistService{
	private final ProcedureCategoryRepository procedureCategoryRepository;
	private final ProcedureRepository procedureRepository;
	private final DeceasedProfileRepository deceasedProfileRepository;

	@Override
	public ChecklistCategoryRes getCategories() {
		List<ChecklistCategoryRes.Category> categories =
				procedureCategoryRepository.findAll().stream()
						.map(c -> new ChecklistCategoryRes.Category(
								c.getProcedureCategoryId(),
								c.getCategoryName()
						))
						.toList();

		return new ChecklistCategoryRes(categories);
	}


}
