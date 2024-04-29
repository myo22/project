package com.example.board.service;

import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.GradeRepository;
import com.example.board.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    private final AssignmentService assignmentService;
    private final AttendanceService attendanceService;
    private final CourseRepository courseRepository;

    @Transactional
    public void calculateTotalScore(User user, Course course){
        int assignmentScore = 0;
        List<AssignmentFile> assignmentFiles = assignmentService.getUserAssignmentFiles(user);
        for(AssignmentFile file : assignmentFiles){
            assignmentScore += file.getScore();
        }

        List<Attendance> attendances = attendanceService.getUserAttendance(user);
        int absenceCount = 0;
        for(Attendance attendance : attendances){
            if(attendance.getAttendanceStatus().equals("결석")){
                absenceCount++;
            }
        }
        int attendanceScore = 30;
        if (absenceCount >= 4) {
            attendanceScore = 0;
        }else{
            for(Attendance attendance : attendances){
                attendanceScore -= attendanceService.calculateAttendanceScore(attendance);
            }
        }
        int totalScore = assignmentScore + attendanceScore;

        String gradeLetter = determineGradeLetter(totalScore);

        Grade grade = Grade.builder()
                .user(user)
                .course(course)
                .attendanceScore(attendanceScore)
                .assignmentScore(assignmentScore)
                .totalScore(totalScore)
                .gradeLetter(gradeLetter)
                .build();

        gradeRepository.save(grade);
    }
}
