package com.example.board.Repository;

import com.example.board.domain.Attendance;
import com.example.board.domain.User;
import com.example.board.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findByUser(User user);
}
