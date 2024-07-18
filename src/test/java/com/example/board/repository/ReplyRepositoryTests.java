package com.example.board.repository;

import com.example.board.Repository.ReplyRepository;
import com.example.board.domain.Board;
import com.example.board.domain.Reply;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert(){

        Long bno = 2L;

        Board board = Board.builder().bno(bno).build();

        Reply reply = Reply.builder()
                .board(board)
                .replyText("테스트")
                .replyer("replyer1")
                .build();

        replyRepository.save(reply);
    }
}
