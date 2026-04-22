package org.accompany.backend.domain.user.service;

import org.accompany.backend.domain.user.dto.response.UserProfileRes;

public interface UserService {
    UserProfileRes getMyProfile(Long userId);
}
