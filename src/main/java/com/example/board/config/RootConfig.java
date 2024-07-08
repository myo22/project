package com.example.board.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 스프링의 설정 클래스 명시
public class RootConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) //필드 매칭 활성화
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) // 필즈 접근 수준 설정
                .setMatchingStrategy(MatchingStrategies.LOOSE); // 매칭 전략 설정, 느슨한 매칭으로 이름이 유사해도 매핑할 수 있게 함.

        return modelMapper;

    }

}
