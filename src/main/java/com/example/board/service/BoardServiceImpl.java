package com.example.board.service;

import com.example.board.Repository.BoardRepository;
import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.domain.Board;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.PageRequestDTO;
import com.example.board.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @PostConstruct
    public void init() {
        modelMapper.createTypeMap(BoardDTO.class, Board.class).addMappings(mapper -> {
            mapper.skip(Board::setCourse);
            mapper.skip(Board::setUser);
        });
    }


    @Override
    public Long register(BoardDTO boardDTO) {
        Board board = modelMapper.map(boardDTO, Board.class);

        Course course = courseRepository.getcourse(boardDTO.getCourseId());
        User user = userRepository.findByUserId(boardDTO.getUserId());

        board.setCourse(course);
        board.setUser(user);

        Long bno = boardRepository.save(board).getBno();

        return bno;
    }



    @Override
    public BoardDTO readOne(Long bno) {
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();

        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

        return boardDTO;
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());
        Board board = result.orElseThrow();

        board.change(boardDTO.getTitle(), board.getContent());

        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {

        boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

//    @Transactional
//    public void addBoard(int userId, int courseId, String title, String content) {
//        User user = userRepository.findById(userId).orElseThrow();
//        Course course = courseRepository.getcourse(courseId);
//        Board board = Board.builder()
//                .user(user)
//                .course(course)
//                .title(title)
//                .content(content)
//                .viewCnt(0)
//                .build();
//
//        boardRepository.save(board);
//    }

    @Transactional(readOnly = true) // select만 할때는 readOnly = true하는게 성능상 좋다.
    public Long getTotalCount() {
        return boardRepository.getBoardCount();
    }

    // 좀 더 좋은 코드는 List를 Page로 고치는게 가장 좋다.
    @Transactional(readOnly = true) // 마찬가지로 읽어오기만 할거니
    public List<Board> getBoards(int page) { // page : 0 - 첫번째 페이지.
        Pageable pageable = PageRequest.of(page, 10);
        return boardRepository.findByOrderByRegDateDesc(pageable).getContent();
    }

    @Transactional
    public Board getBoard(Long boardId) {
//        Board board = boardDao.getBoard(boardId);
//        boardDao.updateViewCnt(boardId);
//        return board;
        return getBoard(boardId, true);
    }

    // updateViewCnt가 true면 글의 조회수를 증가, false면 글의 조회수를 증가하지 않도록 한다.
    @Transactional
    public Board getBoard(Long bno, boolean updateViewCnt) {
        Board board = boardRepository.findById(bno).orElseThrow();
        if (updateViewCnt){ // jpa에서는 트랜잭션이 종료가 될때 -> 메소드가 끝날때 기존 메소드가 변경이 되어 있으면 자동 업데이트가 실행된다.
            board.changeView(board.getViewCnt() + 1); // 해당 메소드가 종료될 때 update가 실행된다.
        }
        return board;
    }

    @Transactional
    public void deleteBoard(int userId, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        if (board.getUser().getUserId() == userId){
            boardRepository.delete(board);
        }
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId); // <- 이런건 jpa 레퍼지토리가 기본으로 제공해주는 메소드이다.
    }

    @Transactional
    public void updateBoard(Long boardId, String title, String content){
        Board board = boardRepository.findById(boardId).orElseThrow();
        board.change(title, content);
        boardRepository.save(board);
    }
}
