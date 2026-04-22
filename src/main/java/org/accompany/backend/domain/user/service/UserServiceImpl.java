package org.accompany.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.dto.response.UserProfileRes;
import org.accompany.backend.domain.user.entity.DeceasedProfile;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.DeceasedProfileRepository;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DeceasedProfileRepository deceasedProfileRepository;

    @Override
    public UserProfileRes getMyProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        DeceasedProfile deceasedProfile =
                deceasedProfileRepository.findByUser_UserId(userId);

        return new UserProfileRes(
                user.getName(),
                user.getEmail(),
                user.getProvider(),
                deceasedProfile != null ? LocalDate.from(deceasedProfile.getDateOfDeath()) : null
        );
    }
}
