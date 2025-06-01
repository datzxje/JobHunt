package com.jobhunt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Value("${keycloak.auth-server-url}")
        private String authServerUrl;

        @Value("${keycloak.realm}")
        private String realm;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        CookieAuthenticationFilter cookieAuthenticationFilter) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/signup",
                                                                "/api/v1/auth/refresh-token",
                                                                "/api/v1/auth/logout",
                                                                "/api/v1/auth/me",
                                                                "/api/v1/auth/reset-password/**",
                                                                "/api/v1/admin/setup-company")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter())))
                                .addFilterBefore(cookieAuthenticationFilter, BasicAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

                // Keycloak stores roles in realm_access.roles
                grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access");
                grantedAuthoritiesConverter.setAuthoritiesClaimDelimiter(" ");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

                // Custom converter to handle nested realm_access.roles
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
                        var realmAccess = jwt.getClaimAsMap("realm_access");
                        if (realmAccess != null && realmAccess.get("roles") instanceof java.util.List<?> roles) {
                                return roles.stream()
                                                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                                "ROLE_" + role))
                                                .collect(java.util.stream.Collectors.toList());
                        }
                        return java.util.Collections.emptyList();
                });

                return jwtAuthenticationConverter;
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                String jwkSetUri = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/certs";
                return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        }
}
