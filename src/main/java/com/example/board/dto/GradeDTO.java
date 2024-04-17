package com.example.board.dto;

import com.example.board.domain.Grade;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class GradeDTO {

    private int gradeId;

    // DTO에서 Integer와 int 차이는 NULL값을 가질 수 있냐 없냐에 차이다.
    private Integer attendanceScore;
    private Integer assignmentScore;
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
