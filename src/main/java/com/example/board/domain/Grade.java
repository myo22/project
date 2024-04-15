package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Integer gradeId;

    @Column(name = "attendance_score")
    private Integer attendanceScore;

    @Column(name = "assignment_score")
    private Integer assignmentScore;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "grade_letter")
    private String gradeLetter;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Builder
    public Grade(int attendanceScore, int assignmentScore, int totalScore, String gradeLetter){
        this.assignmentScore = assignmentScore;
        this.attendanceScore = attendanceScore;
        this.totalScore = totalScore;
        this.gradeLetter = gradeLetter;
    }

}
