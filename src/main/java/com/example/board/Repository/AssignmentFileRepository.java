package com.example.board.Repository;

import com.example.board.domain.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Integer> {

    @Query(value = "select af from AssignmentFile af where  af.assignment.assignmentId = :assignmentId")
    AssignmentFile getAssignmentFile(@Param("assignmentId") int assignmentId);

}
