package org.accompany.backend.domain.checklist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.dto.ProcedureChecklistQueryDto;
import org.accompany.backend.domain.checklist.dto.response.*;
import org.accompany.backend.domain.checklist.entity.UserDocumentChecklist;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserDocumentChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.procedure.entity.Procedure;
import org.accompany.backend.domain.procedure.entity.ProcedureCategory;
import org.accompany.backend.domain.procedure.repository.ProcedureCategoryRepository;
import org.accompany.backend.domain.procedure.repository.ProcedureRepository;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
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
	private final UserDocumentChecklistRepository userDocumentChecklistRepository;


	@Override
	public ChecklistCategoryRes getCategories() {

		log.info("getCategories start");

		List<ChecklistCategoryRes.Category> categories =
				procedureCategoryRepository.findAll().stream()
						.map(c -> new ChecklistCategoryRes.Category(
								c.getProcedureCategoryId(),
								c.getCategoryName()
						))
						.toList();

		log.info("getCategories end - count={}", categories.size());

		return new ChecklistCategoryRes(categories);
	}

	@Override
	public ChecklistCategoryProcedureRes getCategoryProcedures(
			Long categoryId,
			Long userId
	) {

		log.info("[Checklist] getCategoryProcedures START - categoryId={}, userId={}", categoryId, userId);

		// 1. user 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

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
						.map(this::toProcedureRes)
						.toList();

		log.info("[Checklist] getCategoryProcedures END - categoryId={}, profileId={}, count={}", categoryId, profileId, procedures.size() );

		return new ChecklistCategoryProcedureRes(
				category.getProcedureCategoryId(),
				category.getCategoryName(),
				procedures
		);
	}

	private ChecklistCategoryProcedureRes.Procedure toProcedureRes(
			ProcedureChecklistQueryDto dto
	) {

		LocalDateTime dueDate = dto.dueDate();

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
	public ChecklistProcedureDetailRes getProcedureDetail(
			Long procedureId,
			Long userId
	) {
		log.info("[Checklist] getProcedureDetail START - procedureId={}, userId={}", procedureId, userId);

		// 1. user
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 2. active profile
		DeceasedProfile profile = user.getActiveDeceasedProfile();
		if (profile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		Long profileId = profile.getDeceasedProfileId();
		log.debug("[Checklist] ACTIVE PROFILE - profileId={}", profileId);

		// 3. procedure
		Procedure procedure = procedureRepository
				.findWithDetailsByProcedureId(procedureId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PROCEDURE_NOT_FOUND));

		log.debug("[Checklist] PROCEDURE FOUND - name={}", procedure.getProcedureName());

		// 4. checklist
		UserProcedureChecklist checklist =
				userProcedureChecklistRepository.findByProcedureProcedureIdAndDeceasedProfileDeceasedProfileId(
								procedureId,
								profileId
						)
						.orElse(null);

		log.debug("[Checklist] CHECKLIST FOUND = {}", checklist != null);

		// 5. channels
		List<ChecklistProcedureDetailRes.Channel> channels =
				procedure.getProcedureChannels().stream()
						.map(c -> {
							log.debug("[Checklist] CHANNEL - id={}", c.getProcedureChannelId());
							return new ChecklistProcedureDetailRes.Channel(
									c.getProcedureChannelId(),
									c.getChannelType().name(),
									c.getDescription()
							);
						})
						.toList();

		// 6. contacts
		List<ChecklistProcedureDetailRes.Contact> contacts =
				procedure.getProcedureContacts().stream()
						.map(c -> {
							log.debug("[Checklist] CONTACT - id={}", c.getProcedureContactId());
							return new ChecklistProcedureDetailRes.Contact(
									c.getProcedureContactId(),
									c.getTitle(),
									c.getDescription()
							);
						})
						.toList();

		// 7. documents
		List<ChecklistProcedureDetailRes.Document> documents =
				procedure.getProcedureDocuments().stream()
						.map(d -> {

							log.debug("[Checklist] DOCUMENT - id={}, name={}", d.getProcedureDocumentId(), d.getDocumentName());

							UserDocumentChecklist udc =
									userDocumentChecklistRepository
											.findByProcedureDocumentProcedureDocumentIdAndDeceasedProfileDeceasedProfileId(
													d.getProcedureDocumentId(),
													profileId
											)
											.orElse(null);


							return new ChecklistProcedureDetailRes.Document(
									udc != null ? udc.getUserDocumentChecklistId() : null,
									d.getDocumentName(),
									udc != null && udc.isChecked()
							);
						})
						.toList();

		log.info( "[Checklist] getProcedureDetail END - procedureId={}, channels={}, contacts={}, documents={}, checked={}",
				procedureId, channels.size(), contacts.size(), documents.size(), checklist != null && checklist.isChecked()
		);

		return new ChecklistProcedureDetailRes(
				checklist != null ? checklist.getUserProcedureChecklistId() : null,
				procedure.getProcedureId(),
				procedure.getProcedureCategory().getProcedureCategoryId(),

				procedure.getProcedureName(),

				procedure.getDueDateDescription(),
				procedure.getSearchScope(),
				procedure.getCautionText(),

				channels,
				contacts,
				documents,

				checklist != null && checklist.isChecked()
		);

	}


	@Override
	public ChecklistOverallProgressRes getOverallProgress(Long userId) {

		log.info("[CheckList] 전체 진행률 조회 시작 - userId = {}", userId);

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

		log.info("[CheckList] 전체 진행률 조회 완료 - userId = {}, totalCount = {}", userId, totalCount);

		return new ChecklistOverallProgressRes(
				progressRate,
				totalCount,
				completedCount,
				categories
		);
	}

	@Override
	@Transactional
	public void modifyProcedureCheck(Long userProcedureChecklistId, Long userId, boolean isChecked) {

		log.info( "[Checklist] modifyProcedureCheck START - userProcedureChecklistId={}, userId={}, isChecked={}", userProcedureChecklistId, userId, isChecked );

		// 1. user 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 2. active profile
		DeceasedProfile profile = user.getActiveDeceasedProfile();
		if (profile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		// 3. checklist 조회
		UserProcedureChecklist checklist = userProcedureChecklistRepository
				.findByUserProcedureChecklistId(userProcedureChecklistId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CHECKLIST_NOT_FOUND));

		// 4. 권한 체크 (중요)
		if (!checklist.getDeceasedProfile().getDeceasedProfileId()
				.equals(profile.getDeceasedProfileId())) {
			throw new BusinessException(ErrorCode.PROFILE_ACCESS_DENIED);
		}

		// 5. 상태 변경
		checklist.updateCheck(isChecked);

		log.info( "[Checklist] modifyProcedureCheck END - userProcedureChecklistId={}, changedTo={}", userProcedureChecklistId,   checklist.isChecked() );

	}

	@Override
	@Transactional
	public void modifyDocumentCheck(Long procedureDocumentId, Long userId, boolean isChecked) {

		log.info( "[Checklist] modifyDocumentCheck START - procedureDocumentId={}, userId={}, isChecked={}", procedureDocumentId, userId, isChecked);

		// 1. user 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 2. active profile 조회
		DeceasedProfile profile = user.getActiveDeceasedProfile();

		if (profile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		Long profileId = profile.getDeceasedProfileId();

		// 3. 문서 체크리스트 조회
		UserDocumentChecklist checklist =
				userDocumentChecklistRepository
						.findByProcedureDocumentProcedureDocumentIdAndDeceasedProfileDeceasedProfileId(
								procedureDocumentId,
								profileId
						)
						.orElseThrow(() ->
								new BusinessException(ErrorCode.CHECKLIST_NOT_FOUND)
						);

		// 4. 권한 체크
		if (!checklist.getDeceasedProfile().getDeceasedProfileId()
				.equals(profile.getDeceasedProfileId())) {
			throw new BusinessException(ErrorCode.PROFILE_ACCESS_DENIED);
		}

		// 5. 상태 변경
		checklist.updateChecked(isChecked);

		log.info( "[Checklist] modifyDocumentCheck END - procedureDocumentId={}, changedTo={}", procedureDocumentId, checklist.isChecked() );
	}


}
