package com.example.board.service;


import com.example.board.dto.ReplyDTO;

public interface ReplyService {

    Long register(ReplyDTO replyDTO);

    ReplyDTO read(Long rno);

    void Modify(ReplyDTO replyDTO);

    void remove(Long rno);
}
