package com.example.board.service;

import com.example.board.Repository.AttendanceRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.Repository.VideoRepository;
import com.example.board.domain.Attendance;
import com.example.board.domain.User;
import com.example.board.domain.Video;
import com.example.board.dto.AttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public void recordAttendance(int videoId, int userId){
        User user = userRepository.findById(userId).orElseThrow();
        Video video = videoRepository.findById(videoId).orElseThrow();
        Attendance attendance = Attendance.builder()
                .video(video)
                .user(user)
                .attendanceStatus("출석완료")
                .build();
        attendanceRepository.save(attendance);
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
