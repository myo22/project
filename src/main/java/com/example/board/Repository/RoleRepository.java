package com.example.board.Repository;

import com.example.board.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // where name = ?
    Optional<Role> findByName(String name);
}
