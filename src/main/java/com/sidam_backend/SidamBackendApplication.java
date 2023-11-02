package com.sidam_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SidamBackendApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SidamBackendApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SidamBackendApplication.class);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // 해당 요청에 대해 설정
						.allowedOrigins("https://release.d26jxlq0zcsb2a.amplifyapp.com") // 허용할 도메인
						.allowedMethods("*") // 해당 메소드에 대해 설정
						.allowCredentials(true)
						.exposedHeaders("authorization") // 다행이 대소문자는 상관이 없었다.
						.allowedHeaders("*"); // 전달할 헤더
			}
		};
	}
}
