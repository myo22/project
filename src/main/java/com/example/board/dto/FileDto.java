package com.example.board.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private int fileId;
    private String origFilename;
    private String filename;
    private String filePath;
    private int courseId; // 강의 ID
}
