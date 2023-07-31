package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Like {
    private int likeId;
    private int boardId;
    private int userId;
    private int likeCount;
}
