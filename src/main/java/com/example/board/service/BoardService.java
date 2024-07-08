package com.example.board.service;


import com.example.board.dto.BoardDTO;

public interface BoardService{

    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long bno);

    void modify(BoardDTO boardDTO);

    void remove(Long bno);

}
