package com.example.board.repository;

import com.example.board.Repository.BoardRepository;
import com.example.board.domain.Board;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.stream.IntStream;

@Log4j2
@SpringBootTest
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testSearch(){

        // page order by boardId desc
        Pageable pageable = PageRequest.of(1, 10, Sort.by("boardId").descending());

        boardRepository.search(pageable);
    }
}
