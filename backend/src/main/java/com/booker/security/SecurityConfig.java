package com.booker.security;

import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Backend rest no csrtf
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // access rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/hello-world",
                    "/auth/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // no web login and basic auth
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())

            // Bearer token (JWT)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}