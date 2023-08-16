package com.sidam_backend.security;

import com.sidam_backend.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuthSuccessHandler oAuthSuccessHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomAuthenticationManager customAuthenticationManager;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, OAuthSuccessHandler oAuthSuccessHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter, CustomAuthenticationManager customAuthenticationManager) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationManager = customAuthenticationManager;
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
                .requestMatchers("/","/auth/** ","/auth2/**", "/oauth","/login","/sociallogin").permitAll()
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
                .authenticationEntryPoint(customAuthenticationManager);
        http.addFilterAfter(
                jwtAuthenticationFilter,
                CorsFilter.class
        );
//        http.addFilterAfter(
//                jwtAuthenticationFilter,
//                JwtExceptionFilter.class
//        );



        return http.build();
    }
}

