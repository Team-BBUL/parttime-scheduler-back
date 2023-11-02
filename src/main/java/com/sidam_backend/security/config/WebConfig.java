/*
package com.sidam_backend.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer { // 이거 안된다..

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 해당 요청에 대해 설정
                .allowedOrigins("https://release.d26jxlq0zcsb2a.amplifyapp.com") // 허용할 도메인
                .allowedMethods("*") // 해당 메소드에 대해 설정
                .allowCredentials(true) // 도메인의 쿠키 허용
                .allowedHeaders("authorization"); // 전달할 헤더
    }
}
*/
