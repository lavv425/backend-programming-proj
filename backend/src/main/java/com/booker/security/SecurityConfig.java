package com.booker.security;

import com.booker.security.filter.TokenBlacklistFilter;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Main security configuration for the application.
 * Configures authentication, authorization, CORS, and JWT-based security.
 * Enables method-level security with @PreAuthorize annotations.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final TokenBlacklistFilter tokenBlacklistFilter;

    public SecurityConfig(
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler,
            TokenBlacklistFilter tokenBlacklistFilter) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
        this.tokenBlacklistFilter = tokenBlacklistFilter;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * Sets up stateless session management, public endpoints, JWT authentication, and custom error handlers.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Backend rest no csrtf
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // access rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/auth/**", "/error").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Swagger/OpenAPI documentation - public access
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // Admin only - full system management
                .requestMatchers("/roles/**").hasAuthority("SCOPE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("SCOPE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/users").hasAuthority("SCOPE_ADMIN")
                
                // Professional - can manage services and view their appointments
                .requestMatchers(HttpMethod.POST, "/services").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_PROFESSIONAL")
                .requestMatchers(HttpMethod.PUT, "/services/**").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_PROFESSIONAL")
                .requestMatchers(HttpMethod.DELETE, "/services/**").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_PROFESSIONAL")
                .requestMatchers(HttpMethod.GET, "/professionals/**").permitAll()
                
                // Customer - can book appointments and leave reviews
                .requestMatchers(HttpMethod.POST, "/appointments").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/appointments/**").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/reviews").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/reviews/**").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/reviews/**").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_CUSTOMER")
                
                // Payments - customer can create, admin can manage
                .requestMatchers(HttpMethod.POST, "/payments").hasAnyAuthority("SCOPE_ADMIN", "SCOPE_CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/payments/**").hasAuthority("SCOPE_ADMIN")
                
                // Profile management - any authenticated user can manage their own profile
                .requestMatchers(HttpMethod.GET, "/users/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/users/**").authenticated()
                
                // General read access for services, appointments, customers
                .requestMatchers(HttpMethod.GET, "/services/**", "/customers/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/appointments/**", "/reviews/**").authenticated()
                
                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
            )

            // no web login and basic auth
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())

            // Bearer token (JWT)
            .oauth2ResourceServer(oauth2 -> oauth2
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
                .jwt(Customizer.withDefaults())
            )
            
            // Check token blacklist after JWT authentication
            .addFilterAfter(tokenBlacklistFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings.
     * Allows specific HTTP methods and headers for cross-origin requests.
     *
     * @return the configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // config.setAllowedOrigins(List.of(
        //         "http://localhost:5173",
        //         "http://127.0.0.1:5173"
        // ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}