package com.eventra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.eventra.service.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // public AuthenticationProvider authenticationProvider() {
    //     DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    //     authenticationProvider.setUserDetailsService(customUserDetailsService);
    //     authenticationProvider.setPasswordEncoder(passwordEncoder());
    //     return authenticationProvider;
    // }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity, consider enabling for production
    //         .authorizeHttpRequests(authorize -> authorize
    //             .requestMatchers("/api/auth/**").permitAll() // Allow authentication endpoints
    //             .anyRequest().authenticated() // All other requests require authentication
    //         )
    //         .sessionManagement(session -> session
    //             .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions for REST APIs
    //         )
    //         .authenticationProvider(authenticationProvider()); // Set the custom authentication provider
    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // Permit all requests
            );
        return http.build();
    }
}
