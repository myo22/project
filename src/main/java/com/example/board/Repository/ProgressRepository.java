package com.example.board.Repository;

import com.example.board.domain.Course;
import com.example.board.domain.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {

    Progress findByCourseCourseIdAndUserUserId(int courseId, int userId);

    Set<Progress> findByCourseCourseId(int courseId);
}
