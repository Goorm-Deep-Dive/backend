package org.accompany.backend.global.security.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.user.service.UserAuthService;
import org.accompany.backend.global.config.OAuth2Properties;
import org.accompany.backend.global.security.oauth.user.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * OAuth2 로그인 성공 시 JWT를 발급하고
 * RefreshToken을 쿠키에 저장하는 성공 핸들러.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserAuthService userAuthService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2Properties oauth2Properties;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            OAuth2AuthorizationRequest authorizationRequest =
                    authorizationRequestRepository.loadAuthorizationRequest(request);

            String redirectUri = extractRedirectUri(authorizationRequest);

            if (!isAuthorizedRedirectUri(redirectUri)) {
                redirectUri = oauth2Properties.getAuthorizedRedirectUris().get(0);
            }

            OAuth2AuthorizedClient authorizedClient =
                    authorizedClientService.loadAuthorizedClient(
                            oauthToken.getAuthorizedClientRegistrationId(),
                            oauthToken.getName()
                    );

            String providerAccessToken = authorizedClient != null && authorizedClient.getAccessToken() != null
                    ? authorizedClient.getAccessToken().getTokenValue()
                    : null;

            String providerRefreshToken = authorizedClient != null && authorizedClient.getRefreshToken() != null
                    ? authorizedClient.getRefreshToken().getTokenValue()
                    : null;

            userAuthService.loginSuccess(
                    user.getUserId(),
                    user.getRole(),
                    providerAccessToken,
                    providerRefreshToken,
                    response
            );

            log.info("[OAuth2] 로그인 성공 처리 완료 - userId: {}, redirectUri: {}",
                    user.getUserId(), redirectUri);

            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("oauth", "success")
                    .build(true)
                    .toUriString();

            clearAuthenticationAttributes(request);
            authorizationRequestRepository.removeAuthorizationRequest(request, response);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("[OAuth2] 로그인 성공 처리 중 서버 오류 발생", e);
            throw e;
        }
    }

    private String extractRedirectUri(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }

        Map<String, Object> attributes = authorizationRequest.getAttributes();
        Object redirectUri = attributes.get("redirect_uri");

        return redirectUri != null ? redirectUri.toString() : null;
    }

    private boolean isLinkGoogleRequest(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return false;
        }

        Map<String, Object> attributes = authorizationRequest.getAttributes();
        Object linkGoogle = attributes.get("link_google");

        return Boolean.TRUE.equals(linkGoogle);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        if (uri == null || uri.isBlank()) {
            return false;
        }

        try {
            URI clientRedirectUri = URI.create(uri);

            return oauth2Properties.getAuthorizedRedirectUris()
                    .stream()
                    .map(URI::create)
                    .anyMatch(allowedUri ->
                            sameOrigin(allowedUri, clientRedirectUri)
                    );

        } catch (IllegalArgumentException e) {
            log.warn("[OAuth2] 잘못된 redirectUri 형식 - uri: {}", uri);
            return false;
        }
    }

    private boolean sameOrigin(URI allowedUri, URI clientRedirectUri) {
        return allowedUri.getScheme().equalsIgnoreCase(clientRedirectUri.getScheme())
                && allowedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && getPort(allowedUri) == getPort(clientRedirectUri);
    }

    private int getPort(URI uri) {
        if (uri.getPort() != -1) {
            return uri.getPort();
        }

        return switch (uri.getScheme().toLowerCase()) {
            case "https" -> 443;
            case "http" -> 80;
            default -> -1;
        };
    }
}
