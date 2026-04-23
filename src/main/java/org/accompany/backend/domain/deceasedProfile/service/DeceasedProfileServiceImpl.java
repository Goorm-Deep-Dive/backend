package org.accompany.backend.domain.deceasedProfile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileCreateReq;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileRes;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.deceasedProfile.repository.DeceasedProfileRepository;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("[DeceasedProfile] 영면일 최초 저장 시작 - userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[DeceasedProfile] 사용자 조회 실패 - userId={}", userId);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        DeceasedProfile profile = DeceasedProfile.builder()
                .user(user)
                .name(request.name())
                .dateOfDeath(request.dateOfDeath())
                .build();

        deceasedProfileRepository.save(profile);

        if (user.getActiveDeceasedProfile() == null) {
            user.updateActiveDeceasedProfile(profile);
            log.info("[DeceasedProfile] 첫 고인 프로필 생성으로 활성 프로필 설정 - userId={}, deceasedProfileId={}",
                    userId, profile.getDeceasedProfileId());
        }

        log.info("[DeceasedProfile] 영면일 저장 완료  - userId={}, deceasedProfileId={}",
                userId, profile.getDeceasedProfileId());

        return DeceasedProfileRes.from(profile);
    }
}