package com.sidam_backend.security;

import com.sidam_backend.security.config.CorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomAuthenticationManager customAuthenticationManager;

    private final CorsConfig corsConfig;

    public SecurityConfig
            (
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationManager customAuthenticationManager,
            CorsConfig corsConfig) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationManager = customAuthenticationManager;
        this.corsConfig = corsConfig;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.cors();

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .formLogin().disable()
                .httpBasic().disable()

                .authorizeHttpRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers("/","/api/auth/signup", "/api/auth/login").permitAll()
                .requestMatchers("/api/*").permitAll()
                .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationManager)

                /*.and()
                .addFilter(corsConfig.corsFilter())*/

                .and()
                .addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);

//        http.addFilterAfter(
//                jwtAuthenticationFilter,
//                JwtExceptionFilter.class
//        );

        return http.build();
    }
}

