package com.example.board.repository;

import com.example.board.Repository.BoardRepository;
import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.domain.Board;
import com.example.board.domain.BoardImage;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testInsertWithImages(){

        Course course = courseRepository.getcourse(1);
        User user = userRepository.findByUserId(3);

        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .course(course)
                .user(user)
                .build();

        for (int i = 0; i < 3; i++){
            board.addImage(UUID.randomUUID().toString(), "file" + i + "jpg");
        }

        boardRepository.save(board);
    }

    @Test
    public void testReadWithImages(){

        // 반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
        Board board = result.orElseThrow();

        log.info(board);
        log.info("--------------------");
        for (BoardImage boardImage : board.getImageSet()){
            log.info(boardImage);
        }
    }
}
