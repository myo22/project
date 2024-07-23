package com.example.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDTO {

    private Long bno;
    private String title;
    private String name;
    private LocalDateTime regDate;

    private Long viewCnt;

    private Long replyCount;
}
