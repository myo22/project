package com.example.board.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private Long bno;

    @NotEmpty
    @Size(min = 3, max = 100)
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String name; // 추가해줘야한다. Join 했기 때문에

    @Builder.Default
    private int viewCnt = 0;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    @Min(value = 1)
    private int courseId;

    @Min(value = 1)
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