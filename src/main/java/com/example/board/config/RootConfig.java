package com.example.board.config;

import com.example.board.domain.Board;
import com.example.board.domain.Reply;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.ReplyDTO;
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
                .setMatchingStrategy(MatchingStrategies.STRICT); // 매칭 전략 설정, 느슨한 매칭으로 이름이 유사해도 매핑할 수 있게 함. -> 오류가 발생해서 STRICT 바꾸니 해결

        modelMapper.createTypeMap(BoardDTO.class, Board.class).addMappings(mapper -> {
            mapper.skip(Board::setCourse);
            mapper.skip(Board::setUser);
        });

        modelMapper.createTypeMap(Board.class, BoardDTO.class).addMappings(mapper -> {
                mapper.map(src -> src.getUser().getName(), BoardDTO::setName);
        });

        modelMapper.createTypeMap(ReplyDTO.class, Reply.class).addMappings(mapper -> {
            mapper.skip(Reply::setBoard);
        });

        return modelMapper;

    }

}
