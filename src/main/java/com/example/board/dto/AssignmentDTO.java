package com.example.board.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignmentDTO {

    private int assignmentId;

    private String title;

    private String content;

    private int courseId;

}
