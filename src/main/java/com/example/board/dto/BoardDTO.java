package com.example.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private Long bno;
    private String title;
    private String content;
    private String name; // 추가해줘야한다. Join 했기 때문에
    private int viewCnt;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int courseId;
    private int userId;
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