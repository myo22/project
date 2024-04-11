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

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private AttendanceRepository attendanceRepository;
    private UserRepository userRepository;
    private VideoRepository videoRepository;

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
}
