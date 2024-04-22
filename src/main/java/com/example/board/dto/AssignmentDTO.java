package com.example.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignmentDTO {

    private int assignmentId;

    private String title;

    private String content;

    private int maxScore;;

    private LocalDateTime regdate;

    private int courseId;

    private int userId;

}
