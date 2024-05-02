package com.example.board.Repository;

import com.example.board.domain.Course;
import com.example.board.domain.Grade;
import com.example.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
    Grade findByUserAndCourse(User user, Course course);
}
