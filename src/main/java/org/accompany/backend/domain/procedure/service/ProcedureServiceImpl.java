package org.accompany.backend.domain.procedure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.repository.UserDocumentChecklistRepository;
import org.accompany.backend.domain.checklist.entity.UserDocumentChecklist;
import org.accompany.backend.domain.procedure.dto.response.ProcedureDocumentDetailRes;
import org.accompany.backend.domain.procedure.entity.ProcedureDocument;
import org.accompany.backend.domain.procedure.repository.ProcedureDocumentRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProcedureServiceImpl implements ProcedureService {

	private final UserRepository userRepository;
	private final ProcedureDocumentRepository procedureDocumentRepository;
	private final UserDocumentChecklistRepository userDocumentChecklistRepository;

	@Override
	public ProcedureDocumentDetailRes getProcedureDocumentDetail(Long procedureDocumentId, Long userId) {

		log.info(
				"[Procedure] getProcedureDocumentDetail START - procedureDocumentId={}, userId={}",
				procedureDocumentId,
				userId
		);

		// 1. user 조회
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 2. active profile 조회
		DeceasedProfile profile = user.getActiveDeceasedProfile();

		if (profile == null) {
			throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
		}

		Long profileId = profile.getDeceasedProfileId();

		// 3. 문서 조회
		ProcedureDocument document = procedureDocumentRepository.findById(procedureDocumentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND));

		// 4. 사용자 체크리스트 조회
		UserDocumentChecklist checklist =
				userDocumentChecklistRepository
						.findByProcedureDocumentProcedureDocumentIdAndDeceasedProfileDeceasedProfileId(
								procedureDocumentId,
								profileId
						)
						.orElse(null);

		ProcedureDocumentDetailRes response =
				new ProcedureDocumentDetailRes(
						document.getProcedureDocumentId(),
						checklist != null ? checklist.getUserDocumentChecklistId() : null,

						document.getDocumentType(),
						document.getDocumentChannelType(),

						document.getDocumentName(),
						document.getDocumentLocation(),
						document.getDocumentLink(),

						document.getDescription(),

						checklist != null && checklist.isChecked()
				);

		log.info(
				"[Procedure] getProcedureDocumentDetail END - procedureDocumentId={}, checked={}",
				procedureDocumentId,
				response.checked()
		);

		return response;
	}
}
