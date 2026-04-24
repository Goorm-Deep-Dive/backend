package org.accompany.backend.global.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.global.security.handler.JwtAccessDeniedHandler;
import org.accompany.backend.global.security.jwt.JwtAuthenticationEntryPoint;
import org.accompany.backend.global.security.jwt.JwtAuthenticationFilter;
import org.accompany.backend.global.security.oauth.handler.OAuth2AuthenticationFailureHandler;
import org.accompany.backend.global.security.oauth.handler.OAuth2AuthenticationSuccessHandler;
import org.accompany.backend.global.security.oauth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String AUTHORIZATION_BASE_URI = "/api/v1/auth/oauth2/authorization";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                        .requestMatchers("/api/v1/auth/oauth2/refresh", "/login/oauth2/**", "/api/v1/checklists/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri(AUTHORIZATION_BASE_URI)
                                .authorizationRequestResolver(customAuthorizationRequestResolver())
                                .authorizationRequestRepository(authorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        AUTHORIZATION_BASE_URI
                );

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                return customize(defaultResolver.resolve(request), request);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                return customize(defaultResolver.resolve(request, clientRegistrationId), request);
            }

            private OAuth2AuthorizationRequest customize(
                    OAuth2AuthorizationRequest authorizationRequest,
                    HttpServletRequest request
            ) {
                if (authorizationRequest == null) {
                    return null;
                }

                String redirectUri = request.getParameter("redirect_uri");
                String linkGoogle = request.getParameter("link_google");

                Map<String, Object> additionalParameters =
                        new HashMap<>(authorizationRequest.getAdditionalParameters());
                Map<String, Object> attributes =
                        new HashMap<>(authorizationRequest.getAttributes());

                if (redirectUri != null && !redirectUri.isBlank()) {
                    attributes.put("redirect_uri", redirectUri);
                }

                if (isGoogleAuthorizationRequest(request)) {
                    additionalParameters.put("access_type", "offline");
                }

                if ("true".equals(linkGoogle)) {
                    additionalParameters.put("access_type", "offline");
                    additionalParameters.put("prompt", "consent");
                    attributes.put("link_google", true);
                }

                return OAuth2AuthorizationRequest.from(authorizationRequest)
                        .additionalParameters(additionalParameters)
                        .attributes(attributes)
                        .build();
            }

            private boolean isGoogleAuthorizationRequest(HttpServletRequest request) {
                String requestUri = request.getRequestURI();
                return requestUri != null && requestUri.endsWith("/google");
            }
        };
    }
}
