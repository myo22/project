package com.example.board.dto;

import com.example.board.domain.Grade;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class GradeDTO {

    private int gradeId;

    private int attendanceScore;
    private int assignmentScore;
    private int totalScore;
    private String gradeLetter;
    private LocalDateTime createdAt;
    private int userId;
    private int courseId;

    public GradeDTO(Grade grade){
        this.gradeId = grade.getGradeId();
        this.attendanceScore = grade.getAttendanceScore();
        this.assignmentScore = grade.getAssignmentScore();
        this.totalScore = grade.getTotalScore();
        this.gradeLetter = grade.getGradeLetter();
        this.createdAt = grade.getCreatedAt();
        this.userId = grade.getUser().getUserId();
        this.courseId = grade.getCourse().getCourseId();
    }


}
