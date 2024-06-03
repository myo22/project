package com.example.board.Repository;

import com.example.board.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    Participant findByCourseCourseIdAndUserUserId(int courseId, int userId);
}
