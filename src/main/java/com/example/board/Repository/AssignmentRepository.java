package com.example.board.Repository;

import com.example.board.domain.Assignment;
import com.example.board.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

    @Query(value = "select count(a) from Assignment a")
    Long getAssignmentCount();

    Page<Assignment> findByOrderByRegdateDesc(Pageable pageable);

    int countByCourseCourseId(int courseId);
}
