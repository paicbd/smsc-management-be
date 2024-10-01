package com.smsc.management.security;

import com.smsc.management.utils.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AppProperties appProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/handler-status")
                        .permitAll()
                        .requestMatchers(req -> {
                            if (req.getServletPath().startsWith("/ws")) {
                                var header = req.getHeader(appProperties.getWebsocketHeaderName());
                                return (header != null && header.equals(appProperties.getWebsocketHeaderValue()));
                            } else if (req.getServletPath().startsWith("/balance-credit/credit-used")) {
                                var header = req.getHeader(appProperties.getApiKeyHeaderName());
                                return (header != null && header.equals(appProperties.getApiKeyHeaderValue()));
                            }
                            return false;
                        })
                        .permitAll()
                        .anyRequest()
                        .authenticated()).sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
    }
}
