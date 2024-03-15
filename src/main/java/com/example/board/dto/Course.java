package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Course {
    private int courseId;
    private String title;
    private String content;

    private LocalDateTime regdate;
    private int userId;
}
