package com.suraj.cms.config;

import com.suraj.cms.security.ProblemDetailsAccessDeniedHandler;
import com.suraj.cms.security.ProblemDetailsAuthEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


    private final ProblemDetailsAuthEntryPoint authEntryPoint;
    private final ProblemDetailsAccessDeniedHandler accessDeniedHandler;


    public SecurityConfig(ProblemDetailsAuthEntryPoint authEntryPoint, ProblemDetailsAccessDeniedHandler accessDeniedHandler) {
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable()
// your authorization rules here
                .authorizeHttpRequests(reg -> reg.requestMatchers("/api/auth/**").permitAll().anyRequest().authenticated())
// make sure exceptions use our Problem Details adapters
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint).accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }
}