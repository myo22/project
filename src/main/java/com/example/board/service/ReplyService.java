package com.example.board.service;


import com.example.board.dto.PageRequestDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.ReplyDTO;
import org.springframework.data.domain.Page;

public interface ReplyService {

    Long register(ReplyDTO replyDTO);

    ReplyDTO read(Long rno);

    void Modify(ReplyDTO replyDTO);

    void remove(Long rno);

    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);
}
