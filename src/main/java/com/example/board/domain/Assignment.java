package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "assignment")
public class Assignment {

    @Id
    @Column(name = "assignment_id")
    private Integer assignmentId;

    @Column(name = "orig_filename")
    private String origFilename;

    @Column(name = "assignment_name")
    private String assignmentName;

    @Column(name = "assignment_path")
    private String assignmentPath;
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Builder
    public Assignment(String assignmentName, String origFilename, String assignmentPath, String title, String content, User user, Course course){
        this.assignmentName = assignmentName;
        this.assignmentPath = assignmentPath;
        this.origFilename = origFilename;
        this.title = title;
        this.content = content;
        this.user = user;
        this.course = course;
    }
}
