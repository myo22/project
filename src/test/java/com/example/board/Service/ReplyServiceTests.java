package com.example.board.Service;

import com.example.board.dto.ReplyDTO;
import com.example.board.service.ReplyService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;

    @Test
    public void testRegister(){

        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyText("ReplyDTO Test")
                .replyer("replyer")
                .bno(5L)
                .build();

        log.info(replyService.register(replyDTO));
    }

}
