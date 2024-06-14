package com.example.board.dto;

import com.example.board.domain.Video;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private Integer videoId;
    private String videoName;
    private String videoUrl;
    private String title;
    private Integer courseId;
    private String attendanceStatus;
    private Integer userId;
    private String userName;

    public VideoDTO(Video video){
        this.videoId = video.getVideoId();
        this.videoName = video.getVideoName();
        this.videoUrl = video.getVideoUrl();
        this.title = video.getTitle();
        this.courseId = video.getCourse().getCourseId();
        this.userId = video.getUser().getUserId();
        this.userName = video.getUser().getName();
    }
}
