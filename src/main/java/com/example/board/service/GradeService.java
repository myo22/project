package com.example.board.service;

import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.GradeRepository;
import com.example.board.domain.*;
import com.example.board.dto.GradeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    private final AssignmentService assignmentService;
    private final AttendanceService attendanceService;
    private final CourseRepository courseRepository;

    @Transactional
    public Grade calculateTotalScore(User user, Course course){
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

        Grade grade = Grade.builder()
                .user(user)
                .course(course)
                .attendanceScore(attendanceScore)
                .assignmentScore(assignmentScore)
                .totalScore(totalScore)
                .build();

        return  gradeRepository.save(grade);
    }
}
