package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    public Integer attendanceId;

    @CreationTimestamp // 현재시간이 저장될 때 자동으로 생성.
    @Column(name = "attendance_date")
    public LocalDateTime attendanceDate;

    @Column(name = "attendance_status")
    public String attendanceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id") // 동영상 테이블과의 연관 관계를 나타내는 외래 키
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Attendance(int attendanceId, LocalDateTime attendanceDate, String attendanceStatus, Video video, User user){
        this.attendanceId = attendanceId;
        this.attendanceDate = attendanceDate;
        this.attendanceStatus = attendanceStatus;
        this.video = video;
        this.user = user;

    }
}
