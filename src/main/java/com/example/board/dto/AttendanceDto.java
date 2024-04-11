package com.example.board.dto;

import com.example.board.domain.Attendance;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class AttendanceDto {

    private int attendanceId;;

    private LocalDateTime attendanceDate;

    private String attendanceStatus;

    private int videoId;
    private int userId;

    public AttendanceDto(Attendance attendance){
        this.attendanceId = attendance.getAttendanceId();
        this.attendanceStatus = attendance.getAttendanceStatus();
        this.videoId = attendance.getVideo().getVideoId();
        this.userId = attendance.getUser().getUserId();
        this.attendanceDate = attendance.getAttendanceDate();
    }

}
