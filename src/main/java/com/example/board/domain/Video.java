package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "video")
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Integer videoId;

    @Column(name = "video_name")
    private String videoName;


    @Column(name = "video_url")
    private String videoUrl;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Builder
    public Video(int videoId, String videoName, String videoUrl, String title, Course course, User user){
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoUrl = videoUrl;
        this.title = title;
        this.course = course;
        this.user = user;
    }
}
