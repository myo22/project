package com.example.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // '/files/'로 시작하는 경로는 스프링 MVC에서 일반 파일 경로로 처리하도록 지정, 이렇게 안하면 Thymeleaf가 없는 환경에서 스프링 MVC의 모든 경로를 스프링에서 처리하려고 시도하기 때문이다.
        registry.addResourceHandler("/files/**")
                .addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/js/**")
//                .addResourceLocations("classpath:/static/js/");
//        registry.addResourceHandler("/fonts/**")
//                .addResourceLocations("classpath:/static/fonts/");
//        registry.addResourceHandler("/css/**")
//                .addResourceLocations("classpath:/static/css/");
//        registry.addResourceHandler("/assets/**")
//                .addResourceLocations("classpath:/static/assets/");
    }
}
