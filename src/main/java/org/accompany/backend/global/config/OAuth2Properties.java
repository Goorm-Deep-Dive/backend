package org.accompany.backend.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuth2Properties {

    private List<String> authorizedRedirectUris = new ArrayList<>();
}