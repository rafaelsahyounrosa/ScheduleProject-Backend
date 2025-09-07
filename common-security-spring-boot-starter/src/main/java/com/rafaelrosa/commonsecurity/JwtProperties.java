package com.rafaelrosa.commonsecurity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

//TODO checar se está pegando do application.secrets.properties na base do projeto
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String secret,
        @DefaultValue("3600000") long expiration,
        @DefaultValue("90") long clockSkewSeconds
) {}
