package com.example.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Course {
    private int courseId;
    private String title;
    private String content;
}
