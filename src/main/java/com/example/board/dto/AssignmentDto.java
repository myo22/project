package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDto {

    private int assignmentId;

    private String origFilename;

    private String assignmentName;

    private String assignmentPath;

    private String title;

    private String content;

    private int courseId;

    private int userId;
}
