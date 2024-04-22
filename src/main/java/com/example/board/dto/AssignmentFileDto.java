package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentFileDto {

    private int assignmentFileId;

    private String origFilename;

    private int score;

    private String assignmentName;

    private String assignmentPath;

    private int userId;

    private int assignmentId;
}
