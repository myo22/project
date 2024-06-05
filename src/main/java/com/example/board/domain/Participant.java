package com.example.board.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "participant")
@Getter
@Setter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Integer participantId;

    @Column(name = "video_watched", nullable = false)
    private Boolean VideoWatched = false;
    @Column(name = "Assignment_submit", nullable = false)
    private Boolean AssignmentSubmit = false;

    @Column(name = "discussion_watched", nullable = false)
    private Boolean discussionWatched = false;

    @Column(name = "notice_watched", nullable = false)
    private Boolean noticeWatched = false;

    @Column(name = "resource_watched", nullable = false)
    private Boolean resourceWatched = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}