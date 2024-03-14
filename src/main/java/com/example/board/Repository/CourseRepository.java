package com.example.board.Repository;

import com.example.board.domain.Board;
import com.example.board.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query(value = "select c from Course c join fetch c.content")
    List<Course> getCourses();

    // 강의개설자가 쓴 글
    @Query(value = "select c from Course c join fetch c.user u join u.roles r where r.roleId = 2")
    List<Course> getCourse();


}
