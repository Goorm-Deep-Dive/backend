package org.accompany.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.DeceasedProfile;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.DeceasedProfileRepository;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeceasedProfileServiceImpl implements DeceasedProfileService {

    private final DeceasedProfileRepository deceasedProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createDeceasedProfile(Long userId, LocalDate dateOfDeath) {
        log.info("[DeceasedProfile] 최초 영면일 저장 시작 - userId={}, dateOfDeath={}", userId, dateOfDeath);

        if (deceasedProfileRepository.existsByUserUserId(userId)) {
            log.info("[DeceasedProfile] 이미 프로필 존재하여 저장 생략 - userId={}", userId);
            return;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[DeceasedProfile] 사용자 조회 실패 - userId={}", userId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        DeceasedProfile profile = DeceasedProfile.builder()
                .user(user)
                .dateOfDeath(dateOfDeath.atStartOfDay())
                .build();

        deceasedProfileRepository.save(profile);

        log.info("[DeceasedProfile] 최초 영면일 저장 완료 - userId={}, deceasedProfileId={}",
                userId, profile.getDeceasedProfileId());
    }
}