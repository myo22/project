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
    public void saveGrade(Grade grade){

        if (grade == null) {
            throw new IllegalArgumentException("Provided grade is null");
        }

        gradeRepository.save(grade);
    }

    @Transactional
    public Grade findGradeByUserAndCourse(User user, Course course){
        Grade grade = gradeRepository.findByUserAndCourse(user, course);
        return grade;
    }


    @Transactional
    public Grade calculateGrade(User user, Course course){
        Grade existingGrade = gradeRepository.findByUserAndCourse(user, course);

        if (existingGrade == null){
            throw new IllegalStateException("No existing grade record for the given user and course.");
        }


        // 과제 점수
        int assignmentScore = calculateAssignmentScore(user, course);
        // 출석 점수
        int attendanceScore = calculateAttendanceScore(user, course);
        // 총점 점수, 학점 계산
        int totalScore = assignmentScore + attendanceScore;
        String gradeLetter = determineGradeLetter(totalScore);

        existingGrade.setAssignmentScore(assignmentScore);
        existingGrade.setAttendanceScore(attendanceScore);
        existingGrade.setTotalScore(totalScore);
        if(existingGrade.getGradeLetter() == null){
            existingGrade.setGradeLetter(gradeLetter);
        }

        return  existingGrade;
    }

    private int calculateAssignmentScore(User user, Course course){
        int assignmentScore = 0;
        List<AssignmentFile> assignmentFiles = assignmentService.getUserAssignmentFiles(user);
        for(AssignmentFile file : assignmentFiles){
            assignmentScore += file.getScore();
        }
        return assignmentScore;
    }

    private int calculateAttendanceScore(User user, Course course){
        int absenceCount = 0;
        List<Attendance> attendances = attendanceService.getUserAttendance(user);
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
        return attendanceScore;
    }

    private String determineGradeLetter(int totalScore) {
        if (totalScore >= 90) return "A";
        else if (totalScore >= 80) return "B";
        else if (totalScore >= 70) return "C";
        else if (totalScore >= 60) return "D";
        else return "F";
    }

}
