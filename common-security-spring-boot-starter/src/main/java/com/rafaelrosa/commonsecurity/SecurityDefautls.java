package com.rafaelrosa.commonsecurity;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "commmon.security")
public class SecurityDefautls {

    /** Paths públicos (glob) aplicados no shouldNotFilter (ex.: /actuator/**, /auth/**) */
    private List<String> permithPaths = List.of("/actuator/**");

    boolean isPermitted(String path){
        return permithPaths.stream()
                .anyMatch(path::startsWith);
    }

    public List<String> getPermithPaths() {
        return  permithPaths;
    }

    public void setPermithPaths(List<String> p) { this.permithPaths = p; }
}
