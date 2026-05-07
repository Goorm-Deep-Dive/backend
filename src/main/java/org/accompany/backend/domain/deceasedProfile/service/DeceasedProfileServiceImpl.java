package org.accompany.backend.domain.deceasedProfile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileCreateReq;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileUpdateReq;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileListRes;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileRes;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedSurveyStatusRes;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.deceasedProfile.repository.DeceasedProfileRepository;
import org.accompany.backend.domain.survey.service.SurveyService;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeceasedProfileServiceImpl implements DeceasedProfileService {

    private final DeceasedProfileRepository deceasedProfileRepository;
    private final UserRepository userRepository;
    private final UserProcedureChecklistRepository userProcedureChecklistRepository;
    private final SurveyService surveyService;

    @Override
    @Transactional
    public DeceasedProfileRes createDeceasedProfile(Long userId, DeceasedProfileCreateReq request) {
        log.info("[DeceasedProfile] 고인 정보 생성 시작 - userId={}", userId);

        User user = getUser(userId);
        validateDateOfDeath(request.dateOfDeath());

        DeceasedProfile profile = DeceasedProfile.builder()
                .user(user)
                .name(request.name())
                .dateOfDeath(request.dateOfDeath())
                .build();

        deceasedProfileRepository.save(profile);

        user.updateActiveDeceasedProfile(profile);
        log.info("[DeceasedProfile] 고인 정보 생성으로 현재 고인 정보 설정 변경 - userId={}, deceasedProfileId={}",
                userId, profile.getDeceasedProfileId());

        log.info("[DeceasedProfile] 고인 정보 생성 완료 - userId={}, deceasedProfileId={}",
                userId, profile.getDeceasedProfileId());

        return DeceasedProfileRes.from(profile);
    }

    @Override
    public List<DeceasedProfileListRes> getDeceasedProfiles(Long userId) {
        log.info("[DeceasedProfile] 고인 정보 목록 조회 시작 - userId={}", userId);

        User user = getUser(userId);

        Long activeProfileId = user.getActiveDeceasedProfile() != null
                ? user.getActiveDeceasedProfile().getDeceasedProfileId()
                : null;

        List<DeceasedProfile> profiles = deceasedProfileRepository
                .findAllByUserUserIdOrderByDateOfDeathDescCreatedAtDesc(userId);

        return profiles.stream()
                .map(profile -> new DeceasedProfileListRes(
                        profile.getDeceasedProfileId(),
                        profile.getName(),
                        profile.getDateOfDeath(),
                        activeProfileId != null && activeProfileId.equals(profile.getDeceasedProfileId())
                ))
                .sorted((left, right) -> Boolean.compare(right.active(), left.active()))
                .toList();
    }

    @Override
    public DeceasedProfileRes getActiveDeceasedProfile(Long userId) {
        log.info("[DeceasedProfile] 현재 고인 정보 조회 시작 - userId={}", userId);

        User user = getUser(userId);

        DeceasedProfile profile = user.getActiveDeceasedProfile();
        if (profile == null) {
            throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
        }

        return DeceasedProfileRes.from(profile);
    }

    @Override
    @Transactional
    public void modifyDeceasedProfile(Long userId, Long deceasedProfileId, DeceasedProfileUpdateReq request) {
        log.info("[DeceasedProfile] 고인 정보 수정 시작 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);

        DeceasedProfile profile = getOwnedDeceasedProfile(userId, deceasedProfileId);

        validateDateOfDeath(request.dateOfDeath());
        LocalDate beforeDateOfDeath = profile.getDateOfDeath();

        profile.updateDeceasedProfile(request.name(), request.dateOfDeath());

        if (request.dateOfDeath() != null && !beforeDateOfDeath.equals(request.dateOfDeath())) {
            log.info("[DeceasedProfile] 영면일 변경 감지 - userId={}, deceasedProfileId={}, before={}, after={}",
                    userId, deceasedProfileId, beforeDateOfDeath, request.dateOfDeath());

            int updatedCount = updateProcedureChecklistDueDates(profile);

            log.info("[DeceasedProfile] 절차 체크리스트 마감일 재계산 완료 - userId={}, deceasedProfileId={}, updatedCount={}",
                    userId, deceasedProfileId, updatedCount);
        }

        log.info("[DeceasedProfile] 고인 정보 수정 완료 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);
    }

    @Override
    @Transactional
    public void changeActiveDeceasedProfile(Long userId, Long deceasedProfileId) {
        log.info("[DeceasedProfile] 현재 고인 정보 변경 시작 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);

        User user = getUser(userId);
        DeceasedProfile profile = getOwnedDeceasedProfile(userId, deceasedProfileId);

        user.updateActiveDeceasedProfile(profile);

        log.info("[DeceasedProfile] 현재 고인 정보 변경 완료 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);
    }

    @Override
    public DeceasedSurveyStatusRes getDeceasedSurveyStatus(Long userId) {
        log.info("[DeceasedProfile] 현재 고인 정보 설문조사 상태 조회 - userId={}", userId);

        User user = getUser(userId);
        DeceasedProfile profile = user.getActiveDeceasedProfile();

        if (profile == null) {
            throw new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
        }

        return new DeceasedSurveyStatusRes(profile.getSurveyStatus());
    }

    @Override
    @Transactional
    public void deleteDeceasedProfile(Long userId, Long deceasedProfileId) {
        log.info("[DeceasedProfile] 삭제 요청 시작 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);

        User user = getUser(userId);
        DeceasedProfile activeDeceasedProfile = user.getActiveDeceasedProfile();

        if(activeDeceasedProfile.getDeceasedProfileId().equals(deceasedProfileId)){
            log.warn("[DeceasedProfile] 활성 고인 정보 삭제 시도 차단 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ACTIVE_DECEASED_PROFILE);
        }

        DeceasedProfile profile = getOwnedDeceasedProfile(userId, deceasedProfileId);

        log.info("[DeceasedProfile] 삭제 대상 조회 완료 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);

        deceasedProfileRepository.delete(profile);

        log.info("[DeceasedProfile] 삭제 완료 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);
    }

    private int updateProcedureChecklistDueDates(DeceasedProfile profile) {

        List<UserProcedureChecklist> checklists =
                userProcedureChecklistRepository.findAllWithProcedureByDeceasedProfileId(
                        profile.getDeceasedProfileId()
                );

        checklists.forEach(checklist ->
                checklist.updateDueDate(
                        surveyService.calculateDueDate(
                                checklist.getProcedure(),
                                profile.getDateOfDeath()
                        )
                )
        );

        return checklists.size();
    }

    private User getUser(Long userId) {
        return userRepository.findByIdWithActiveDeceasedProfile(userId)
                .orElseThrow(() -> {
                    log.error("[DeceasedProfile] 사용자 조회 실패 - userId={}", userId);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
    }

    private DeceasedProfile getOwnedDeceasedProfile(Long userId, Long deceasedProfileId) {
        return deceasedProfileRepository.findByDeceasedProfileIdAndUserUserId(deceasedProfileId, userId)
                .orElseThrow(() -> {
                    log.error("[DeceasedProfile] 사용자 소유 고인 정보 조회 실패 - userId={}, deceasedProfileId={}", userId, deceasedProfileId);
                    return new BusinessException(ErrorCode.DECEASED_PROFILE_NOT_FOUND);
                });
    }

    private void validateDateOfDeath(LocalDate dateOfDeath) {
        if (dateOfDeath != null && dateOfDeath.isAfter(LocalDate.now())) {
            throw new BusinessException(ErrorCode.INVALID_DATE_OF_DEATH);
        }
    }

}
