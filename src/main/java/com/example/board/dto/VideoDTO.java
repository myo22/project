package com.example.board.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class VideoDTO {

    private Integer videoId;
    private String videoName;
    private String videoUrl;
    private String title;
    private Integer courseId;
    private Integer userId;
}
