package com.rafaelrosa.commonsecurity;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ConditionalOnClass(RequestInterceptor.class)
public class FeignJwtRelayConfig {

    @Bean
    public RequestInterceptor jwtRelayInterceptor() {
        return template -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // Se armazena o token original em algum lugar do request, recupere-o aqui.
            // Como alternativa, pode usar um once-per-request filter que capture o token e jogue num ThreadLocal.
            // Exemplo simples (se tiver salvo o header bruto em RequestContext):
            // template.header("Authorization", "Bearer " + token);
        };
    }
}