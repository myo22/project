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
    private boolean VideoWatched = false;
    @Column(name = "Assignment_submit", nullable = false)
    private boolean AssignmentSubmit = false;

    @Column(name = "discussion_watched", nullable = false)
    private boolean discussionWatched = false;

    @Column(name = "notice_watched", nullable = false)
    private boolean noticeWatched = false;

    @Column(name = "resource_watched", nullable = false)
    private boolean resourceWatched = false;

}
