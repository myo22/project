package com.example.board.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private Long fileId;
    private String origFilename;
    private String filename;
    private String filePath;
    private Long courseId; // 강의 ID
}
