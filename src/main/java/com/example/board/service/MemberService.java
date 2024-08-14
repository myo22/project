package com.example.board.service;

import com.example.board.dto.MemberJoinDTO;

public interface MemberService {

    // 같은 아이디가 존재할경우 예외를 발생
    static class MidExistException extends Exception {

    }

    void join (MemberJoinDTO memberJoinDTO) throws MidExistException;
}
