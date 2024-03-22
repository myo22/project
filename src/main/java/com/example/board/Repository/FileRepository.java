package com.example.board.Repository;

import com.example.board.domain.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<AttachedFile, Integer> {
}
