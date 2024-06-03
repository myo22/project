package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "progress")
@Getter
@Setter
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer progressId;

    @Column(name = "total_videos", nullable = false)
    private Integer totalVideos = 0;

    @Column(name = "total_assignments", nullable = false)
    private Integer totalAssignments = 0;

    @Column(name = "total_discussions", nullable = false)
    private Integer totalDiscussions = 0;

    @Column(name = "total_notices", nullable = false)
    private Integer totalNotices = 0;

    @Column(name = "total_resources", nullable = false)
    private Integer totalResources = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Progress(int totalVideos, int totalAssignments, int totalDiscussions, int totalNotices, int totalResources, Course course, User user){
        this.totalVideos = totalVideos;
        this.totalAssignments = totalAssignments;
        this.totalDiscussions = totalDiscussions;
        this.totalNotices = totalNotices;
        this.totalResources = totalResources;
        this.course = course;
        this.user = user;
    }

}
