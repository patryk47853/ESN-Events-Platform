package com.esn.event.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final String jwtSecret;

    public SecurityConfig(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/events",
                                "/api/events/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/events"
                        ).hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/events/**"
                        ).hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/events/*/reserve",
                                "/api/events/*/release"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )

                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        byte[] keyBytes = Base64.getDecoder()
                .decode(jwtSecret);

        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .build();
    }

    private Converter<Jwt, AbstractAuthenticationToken>
    jwtAuthenticationConverter() {

        return jwt -> {

            String role = jwt.getClaimAsString("role");

            List<SimpleGrantedAuthority> authorities =
                    role == null
                            ? List.of()
                            : List.of(new SimpleGrantedAuthority("ROLE_" + role));

            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        };
    }
}