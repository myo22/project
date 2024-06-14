package com.example.board.service;

import com.example.board.Repository.AssignmentFileRepository;
import com.example.board.Repository.AttendanceRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.Repository.VideoRepository;
import com.example.board.domain.*;
import com.example.board.dto.AttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AssignmentService assignmentService;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public void recordAttendance(int videoId, int userId){
        User user = userRepository.findById(userId).orElseThrow();
        Video video = videoRepository.findById(videoId).orElseThrow();

        Attendance attendanceExist = attendanceRepository.findByVideoVideoIdAndUserUserId(videoId, userId);
        if(attendanceExist != null){
            return;
        }

        Attendance attendance = Attendance.builder()
                .video(video)
                .user(user)
                .attendanceStatus("출석완료")
                .build();
        attendanceRepository.save(attendance);
    }

    @Transactional
    public List<Attendance> getUserAttendance(User user){
        List<Attendance> attendances = attendanceRepository.findByUser(user);
        return attendances;
    }

    @Transactional
    public int calculateAttendanceScore(Attendance attendance){
        switch (attendance.getAttendanceStatus()){
            case "출석완료":
                return 0;
            case "지각":
                return 1;
            case "결석":
                return 2;
            default:
                return 3;
        }
    }

}
