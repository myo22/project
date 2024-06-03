package com.example.board.Repository;

import com.example.board.domain.Course;
import com.example.board.domain.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {

    Progress findByCourseCourseIdAndUserUserId(int courseId, int userId);
}
