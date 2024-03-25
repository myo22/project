package com.example.board.Repository;

import com.example.board.domain.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<AssignmentFile, Integer> {
}
