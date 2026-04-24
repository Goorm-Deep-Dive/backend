package org.accompany.backend.domain.checklist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.dto.ProcedureChecklistQueryDto;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProcedureRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryProgressRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistCategoryRes;
import org.accompany.backend.domain.checklist.dto.response.ChecklistOverallProgressRes;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.procedure.entity.ProcedureCategory;
import org.accompany.backend.domain.procedure.repository.ProcedureCategoryRepository;
import org.accompany.backend.domain.procedure.repository.ProcedureRepository;
import org.accompany.backend.domain.procedure.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChecklistServiceImpl implements ChecklistService {
	private final ProcedureCategoryRepository procedureCategoryRepository;
	private final ProcedureRepository procedureRepository;
	private final UserRepository userRepository;
	private final UserProcedureChecklistRepository userProcedureChecklistRepository;


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

	@Override
	public ChecklistCategoryProcedureRes getCategoryProcedures(
			Long categoryId,
			Long userId
	) {
		// 1. user 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		log.debug("getCategoryProcedures {}", user.getUserId());

		// 2. active profile 꺼내기
		DeceasedProfile profile = user.getActiveDeceasedProfile();

		if (profile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		Long profileId = profile.getDeceasedProfileId();

		// 3. 카테고리 조회
		ProcedureCategory category = procedureCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

		// 4. 절차 조회
		List<ProcedureChecklistQueryDto> rows =
				procedureRepository.findProceduresWithChecklist(categoryId, profileId);

		// 5. DTO 변환
		List<ChecklistCategoryProcedureRes.Procedure> procedures =
				rows.stream()
						.map(dto -> toProcedureRes(dto, profile))
						.toList();

		return new ChecklistCategoryProcedureRes(
				category.getProcedureCategoryId(),
				category.getCategoryName(),
				procedures
		);
	}

	private LocalDateTime calculateDueDate(
			ProcedureChecklistQueryDto dto,
			DeceasedProfile profile
	) {
		if (dto.baseDueDate() == null || dto.dueDateUnit() == null) {
			return null;
		}

		LocalDateTime base = profile.getDateOfDeath().atStartOfDay();
		int amount = dto.baseDueDate();

		return switch (dto.dueDateUnit()) {
			case DAY -> base.plusDays(amount);
			case MONTH -> base.plusMonths(amount);
			case YEAR -> base.plusYears(amount);
		};
	}


	private ChecklistCategoryProcedureRes.Procedure toProcedureRes(
			ProcedureChecklistQueryDto dto,
			DeceasedProfile profile
	) {

		LocalDateTime dueDate = dto.dueDate();

		if (dueDate == null) {
			dueDate = calculateDueDate(dto, profile);
		}

		return new ChecklistCategoryProcedureRes.Procedure(
				dto.userProcedureChecklistId(),
				dto.procedureId(),
				dto.procedureName(),
				calculateRemainingDays(dueDate),
				Boolean.TRUE.equals(dto.isChecked())
		);
	}

	private Integer calculateRemainingDays(LocalDateTime dueDate) {
		if (dueDate == null) return null;

		LocalDate today = LocalDate.now();
		LocalDate target = dueDate.toLocalDate();

		return (int) ChronoUnit.DAYS.between(today, target);
	}

	@Override
	public ChecklistOverallProgressRes getOverallProgress(Long userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		DeceasedProfile profile = user.getActiveDeceasedProfile();

		if (profile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		Long deceasedProfileId = profile.getDeceasedProfileId();

		List<ChecklistCategoryProgressRes> checklistCategoryProgressResList
				= userProcedureChecklistRepository.findCategoryProgresses(deceasedProfileId);

		int totalCount = checklistCategoryProgressResList.stream()
				.mapToInt(ChecklistCategoryProgressRes::totalCount)
				.sum();

		int completedCount = checklistCategoryProgressResList.stream()
				.mapToInt(ChecklistCategoryProgressRes::completedCount)
				.sum();

		int progressRate = totalCount == 0 ? 0 : (completedCount * 100) / totalCount;

		List<ChecklistCategoryProgressRes> categories = checklistCategoryProgressResList.stream()
				.map(category -> new ChecklistCategoryProgressRes(
						category.categoryId(),
						category.categoryName(),
						category.totalCount() == 0 ? 0 : (category.completedCount() * 100) / category.totalCount(),
						category.totalCount(),
						category.completedCount()
				))
				.toList();

		return new ChecklistOverallProgressRes(
				progressRate,
				totalCount,
				completedCount,
				categories
		);
	}
}
