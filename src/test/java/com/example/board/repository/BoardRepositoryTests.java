package com.example.board.repository;

import com.example.board.Repository.BoardRepository;
import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.ReplyRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
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

    @Autowired
    private ReplyRepository replyRepository;

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

    @Test
    public void testModifyImages(){

        Optional<Board> result = boardRepository.findByIdWithImages(1L);
        Board board = result.orElseThrow();

        board.clearImages();

        for (int i = 0; i < 2; i++){

             board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }

        boardRepository.save(board);
    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){

        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);
    }

    @Test
    public void testInsertALl(){

        Course course = courseRepository.getcourse(1);
        User user = userRepository.findByUserId(3);

        for (int i = 1; i < 100; i++){

            Board board = Board.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .course(course)
                    .user(user)
                    .build();

            for (int j = 0; j < 3; j++){

                if (i % 5 == 0){
                    continue;
                }

                board.addImage(UUID.randomUUID().toString(), i+"file"+j+".jpg");

            }
            boardRepository.save(board);
        }
    }

    @Test
    @Transactional
    public void testSearchImageReplyCount(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        boardRepository.searchWithAll(null, null, pageable);
    }
}
