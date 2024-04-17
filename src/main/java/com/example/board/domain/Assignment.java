package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private Integer assignmentId;

    private String title;

    private String content;

    @Column(name = "max_score")
    private Integer maxScore;
    private Integer score;

    @CreationTimestamp
    private LocalDateTime regdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "assignment")
    private List<AssignmentFile> assignmentFiles = new ArrayList<>();

    @Builder
    public Assignment(String title, String content, int maxScore, int score, Course course, User user){
        this.title = title;
        this.content = content;
        this.maxScore = maxScore;
        this.score = score;
        this.course = course;
        this.user = user;
    }

}
