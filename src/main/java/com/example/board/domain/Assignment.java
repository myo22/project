package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "assignment")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private int assignmentId;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "assignment")
    private List<AssignmentFile> assignmentFiles = new ArrayList<>();

    @Builder
    public Assignment(int assignmentId, String title, String content, Course course){
        this.assignmentId = assignmentId;
        this.title = title;
        this.content = content;
        this.course = course;
    }

}
