package com.example.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Board {
    private int boardId;
    private String title;
    private String content;
    private String name; // 추가해줘야한다. Join 했기 때문에
    private int courseId;
    private int userId;
    private LocalDateTime regdate;
    private int viewCnt;



}

// b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name

/*
'board_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
'title', 'varchar(100)', 'NO', '', NULL, ''
'ccontent', 'text', 'YES', '', NULL, ''
'user_id', 'int', 'NO', 'PRI', NULL, ''
'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
'view_cnt', 'int', 'YES', '', '0', ''

 */