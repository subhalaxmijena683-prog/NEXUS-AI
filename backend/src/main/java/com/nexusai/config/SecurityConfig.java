package com.nexusai.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Health check
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/health"
                ).permitAll()

                // User registration
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/users/register"
                ).permitAll()

                // User login
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/users/login"
                ).permitAll()

                // Temporary public access for PDF extraction testing
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/pdf/extract"
                ).permitAll()

                // Temporary public access for AI chat testing
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/ai/chat"
                ).permitAll()

                // Temporary public access for Research Agent testing
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/agents/research"
                ).permitAll()

                // Temporary public access for Data Analyst Agent testing
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/agents/data-analyst"
                ).permitAll()

                // Temporary public access for RAG Knowledge Agent testing
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/agents/rag"
                ).permitAll()

                // All other APIs require JWT authentication
                .anyRequest().authenticated()
            )

            .httpBasic(httpBasic ->
                httpBasic.disable()
            )

            .formLogin(formLogin ->
                formLogin.disable()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}