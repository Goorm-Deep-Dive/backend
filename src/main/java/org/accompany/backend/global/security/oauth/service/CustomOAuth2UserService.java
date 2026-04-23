package org.accompany.backend.global.security.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.Provider;
import org.accompany.backend.domain.user.entity.Role;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.entity.UserStatus;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.security.oauth.user.CustomOAuth2User;
import org.accompany.backend.global.security.oauth.userinfo.OAuth2UserInfo;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("[OAuth2] 로그인 사용자 정보 조회 시작");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Provider provider = getValidatedProvider(userRequest);
        OAuth2UserInfo userInfo = createUserInfo(provider, oAuth2User);
        String providerUserId = getValidatedProviderUserId(userInfo);
        Optional<User> optionalUser = findUser(userInfo, providerUserId);
        boolean isNewUser = optionalUser.isEmpty();
        User user = optionalUser.orElseGet(() -> createNewUser(userInfo, providerUserId));

        if (!isNewUser) {
            updateExistingUser(user, userInfo);
        }

        log.info("[OAuth2] 로그인 처리 완료 - userId: {}, newUser: {}", user.getUserId(), isNewUser);

        return createPrincipal(user, providerUserId, isNewUser, oAuth2User);
    }

    private Provider getValidatedProvider(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        try {
            return Provider.fromRegistrationId(registrationId);
        } catch (IllegalArgumentException e) {
            log.warn("[OAuth2] 지원하지 않는 소셜 로그인 요청 - provider: {}", registrationId);
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }
    }

    private OAuth2UserInfo createUserInfo(Provider provider, OAuth2User oAuth2User) {
        return OAuth2UserInfo.of(provider, oAuth2User.getAttributes());
    }

    private Optional<User> findUser(OAuth2UserInfo userInfo, String providerUserId) {
        return userRepository.findByProviderAndProviderUserId(
                userInfo.getProvider(),
                providerUserId
        );
    }

    private User createNewUser(OAuth2UserInfo userInfo, String providerUserId) {
        log.info("[OAuth2] 신규 회원 가입 진행 - provider: {}", userInfo.getProvider());

        User user = User.builder()
                .provider(userInfo.getProvider())
                .providerUserId(providerUserId)
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .isNotificationEnabled(true)
                .build();

        syncGoogleAccountIfNeeded(user, userInfo);

        return userRepository.save(user);
    }

    private void updateExistingUser(User user, OAuth2UserInfo userInfo) {
        log.info("[OAuth2] 기존 회원 로그인 - userId: {}", user.getUserId());

        user.updateProfile(
                userInfo.getEmail(),
                userInfo.getName()
        );

        syncGoogleAccountIfNeeded(user, userInfo);
    }

    private void syncGoogleAccountIfNeeded(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getProvider() == Provider.GOOGLE) {
            user.connectGoogleAccount(userInfo.getProviderId());
        }
    }

    private CustomOAuth2User createPrincipal(User user, String providerUserId, boolean isNewUser, OAuth2User oAuth2User) {
        return new CustomOAuth2User(
                user.getUserId(),
                user.getEmail(),
                user.getRole(),
                providerUserId,
                isNewUser,
                oAuth2User.getAttributes()
        );
    }

    private String getValidatedProviderUserId(OAuth2UserInfo userInfo) {

        String providerUserId = userInfo.getProviderId();

        if (providerUserId == null || providerUserId.isBlank()) {

            log.warn("[OAuth2] 소셜 사용자 식별값이 없음 - provider: {}", userInfo.getProvider());
            throw new OAuth2AuthenticationException("소셜 사용자 식별값이 없습니다.");
        }

        return providerUserId;
    }
}
