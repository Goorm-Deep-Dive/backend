package org.accompany.backend.domain.deceasedProfile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileCreateReq;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileUpdateReq;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileListRes;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileRes;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.deceasedProfile.repository.DeceasedProfileRepository;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeceasedProfileServiceImpl implements DeceasedProfileService {

    private final DeceasedProfileRepository deceasedProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DeceasedProfileRes createDeceasedProfile(Long userId, DeceasedProfileCreateReq request) {
        log.info("[DeceasedProfile] 고인 정보 생성 시작 - userId={}", userId);

        User user = getUser(userId);

        DeceasedProfile profile = DeceasedProfile.builder()
                .user(user)
                .name(request.name())
                .dateOfDeath(request.dateOfDeath())
                .build();

        deceasedProfileRepository.save(profile);

        if (user.getActiveDeceasedProfile() == null) {
            user.updateActiveDeceasedProfile(profile);
            log.info("[DeceasedProfile] 첫 고인 정보 생성으로 현재 고인 정보 설정 - userId={}, deceasedProfileId={}",
                    userId, profile.getDeceasedProfileId());
        }

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

        profile.updateDeceasedProfile(request.name(), request.dateOfDeath());

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

    private User getUser(Long userId) {
        return userRepository.findById(userId)
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

}
