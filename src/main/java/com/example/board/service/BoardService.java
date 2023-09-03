package com.example.board.service;

import com.example.board.Repository.BoardRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.domain.Board;
import com.example.board.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addBoard(int userId, String title, String content) {
        User user = userRepository.findById(userId).orElseThrow();
        Board board = new Board();
        board.setUser(user);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        boardRepository.save(board);
    }

    @Transactional(readOnly = true) // select만 할때는 readOnly = true하는게 성능상 좋다.
    public Long getTotalCount() {
        return boardRepository.getBoardCount();
    }

    // 좀 더 좋은 코드는 List를 Page로 고치는게 가장 좋다.
    @Transactional(readOnly = true) // 마찬가지로 읽어오기만 할거니
    public List<Board> getBoards(int page) { // page : 0 - 첫번째 페이지.
        Pageable pageable = PageRequest.of(page, 10);
        return boardRepository.findByOrderOrderByRegdateDesc(pageable).getContent();
    }

    @Transactional
    public Board getBoard(int boardId) {
//        Board board = boardDao.getBoard(boardId);
//        boardDao.updateViewCnt(boardId);
//        return board;
        return getBoard(boardId, true);
    }

    // updateViewCnt가 true면 글의 조회수를 증가, false면 글의 조회수를 증가하지 않도록 한다.
    @Transactional
    public Board getBoard(int boardId, boolean updateViewCnt) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        if (updateViewCnt){ // jpa에서는 트랜잭션이 종료가 될때 -> 메소드가 끝날때 기존 메소드가 변경이 되어 있으면 자동 업데이트가 실행된다.
            board.setViewCnt(board.getViewCnt() + 1); // 해당 메소드가 종료될 때 update가 실행된다.
        }
        return board;
    }

    @Transactional
    public void deleteBoard(int userId, int boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        if (board.getUser().getUserId() == userId){
            boardRepository.delete(board);
        }
    }

    @Transactional
    public void deleteBoard(int boardId) {
        boardRepository.deleteById(boardId); // <- 이런건 jpa 레퍼지토리가 기본으로 제공해주는 메소드이다.
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content){
        Board board = boardRepository.findById(boardId).orElseThrow();
        board.setTitle(title);
        board.setContent(content);
    }
}
