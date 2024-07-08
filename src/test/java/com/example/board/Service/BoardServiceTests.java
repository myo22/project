package com.example.board.Service;

import com.example.board.dto.BoardDTO;
import com.example.board.service.BoardService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister(){

        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("테스트")
                .content("테스트내용")
                .name("나다")
                .build();

        Long bno = boardService.register(boardDTO);

        log.info("bno: " + bno);
    }
}
