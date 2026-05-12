package org.accompany.backend.global.security.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.entity.Provider;
import org.accompany.backend.domain.user.event.UserEvent;
import org.accompany.backend.global.config.OAuth2Properties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2Properties oauth2Properties;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        OAuth2AuthorizationRequest authorizationRequest =
                authorizationRequestRepository.loadAuthorizationRequest(request);

        String redirectUri = extractRedirectUri(authorizationRequest);

        if (!isAuthorizedRedirectUri(redirectUri)) {
            redirectUri = oauth2Properties.getAuthorizedRedirectUris().get(0);
        }

        String targetUrl = UriComponentsBuilder
                .fromUriString(redirectUri)
                .queryParam("error", "oauth2_login_failed")
                .build()
                .toUriString();

        String deviceId = authorizationRequest != null
                ? (String) authorizationRequest.getAttributes().get("deviceId") : null;

        String registrationId = request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1);
        Provider provider = Provider.fromRegistrationId(registrationId);
        applicationEventPublisher.publishEvent(UserEvent.failure(deviceId, provider, exception.getMessage()));

        log.error("[OAuth2] 로그인 실패 - message: {}", exception.getMessage(), exception);

        authorizationRequestRepository.removeAuthorizationRequest(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractRedirectUri(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }

        Map<String, Object> attributes = authorizationRequest.getAttributes();
        Object redirectUri = attributes.get("redirect_uri");

        return redirectUri != null ? redirectUri.toString() : null;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        if (uri == null || uri.isBlank()) {
            return false;
        }

        return oauth2Properties.getAuthorizedRedirectUris()
                .stream()
                .anyMatch(uri::equals);
    }
}