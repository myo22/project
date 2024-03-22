package com.example.board.Repository;

import com.example.board.domain.Board;
import com.example.board.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    // Course 엔티티와 관련된 Repository를 정의합니다.

    // Course 엔티티를 조회할 때 fetch join을 사용하여 관련된 엔티티(User)를 함께 가져옵니다.
    @Query(value = "select c from Course c join fetch c.user")
    List<Course> getCourses();

    @Query(value = "select c from Course c where c.courseId = :courseId")
    Course getcourse(@Param("courseId") int courseId);

    Page<Course> findByOrderByRegdateDesc(Pageable pageable);

    // Course 테이블의 레코드 수를 가져옵니다.
    @Query(value = "select count(c) from Course c")
    Long getCourseCount();

    // 관리자가 쓴 강의만 목록을 가져옵니다.
    @Query(value = "select c from Course c join fetch c.user u join u.roles r where r.roleId = 2")
    List<Course> getCoursesByAdmin();

    // roleName에 해당하는 역할을 가진 사용자가 쓴 강의 목록을 가져옵니다.
    @Query(value = "select c from Course c join fetch c.user u join u.roles r where r.name = :roleName")
    List<Course> getCoursesByRole(@Param("roleName") String roleName);
}
