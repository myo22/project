package com.example.board.Repository;

import com.example.board.domain.Assignment;
import com.example.board.domain.AssignmentFile;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.Assign;

import java.util.List;

public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Integer> {

    // 아래는 쿼리로 직접 정의한거고
    @Query(value = "select af from AssignmentFile af where  af.assignment.assignmentId = :assignmentId")
    AssignmentFile getAssignmentFile(@Param("assignmentId") int assignmentId);

    void deleteByAssignmentAssignmentId(int assignmentId);

    AssignmentFile findByAssignmentAssignmentId(int assignmentId);

    AssignmentFile findByUserUserId(int userId);

    // 이거는 Spring Data JPA가 제공하는 메서드 네이밍 규칙을 활용한 것이다.
    List<AssignmentFile> findByAssignment_AssignmentId(int assignmentId);
    List<AssignmentFile> findByUser(User user);
}
