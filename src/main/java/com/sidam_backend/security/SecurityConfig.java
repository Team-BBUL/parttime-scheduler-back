package com.sidam_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuthSuccessHandler oAuthSuccessHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, OAuthSuccessHandler oAuthSuccessHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeHttpRequests()
                .requestMatchers("/","/auth/** ","/auth2/**").permitAll()
                .requestMatchers("/api").permitAll()
                .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/auth/authorize")
                .and()
                    .redirectionEndpoint()
                    .baseUri("/oauth/callback**")
                    .and()
                    .userInfoEndpoint().userService(customOAuth2UserService)
                .and()
                .successHandler(oAuthSuccessHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint());

        http.addFilterAfter(
                jwtAuthenticationFilter,
                CorsFilter.class
        );


        return http.build();
    }
}

