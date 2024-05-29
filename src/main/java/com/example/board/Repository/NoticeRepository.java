package com.example.board.Repository;

import com.example.board.domain.Course;
import com.example.board.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findByCourse(Course course);
}
