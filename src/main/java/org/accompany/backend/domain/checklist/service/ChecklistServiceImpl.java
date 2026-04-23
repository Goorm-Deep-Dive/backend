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

//	@Override
//	public ChecklistCategoryProcedureRes getCategoryProcedures(
//			Long categoryId,
//			Long profileId,
//			Long userId
//	) {
//		// 1. 로그인 유저 기준으로 deceasedProfile 조회
//		DeceasedProfile profile = deceasedProfileRepository
//				.findByDeceasedProfileIdAndUserUserId(profileId, userId)
//				.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_ACCESS_DENIED));
//
//		// 2. 카테고리 조회
//		ProcedureCategory category = procedureCategoryRepository.findById(categoryId)
//				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
//
//		//  3. 절차 조회
//		List<Object[]> rows =
//				procedureRepository.findProceduresWithChecklist(categoryId, profileId);
//
//		List<ChecklistCategoryProcedureRes.Procedure> procedures =
//				rows.stream()
//						.map(row -> {
//							Procedure p = (Procedure) row[0];
//							UserProcedureChecklist upc = (UserProcedureChecklist) row[1];
//
//							return new ChecklistCategoryProcedureRes.Procedure(
//									upc != null ? upc.getUserProcedureChecklistId() : null,
//									p.getProcedureId(),
//									p.getProcedureName(),
//									calculateRemainingDays(upc),
//									upc != null && upc.isCheck()
//							);
//						})
//						.toList();
//		return new ChecklistCategoryProcedureRes(
//				category.getProcedureCategoryId(),
//				category.getCategoryName(),
//				procedures
//		);
//
//	}
}
