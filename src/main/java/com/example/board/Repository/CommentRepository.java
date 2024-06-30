package com.example.board.Repository;

import com.example.board.domain.*;
import com.example.board.dto.CommentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Set<Comment> findByAssignmentAssignmentIdOrderByCreatedAtDesc(int assignmentId);

    Set<Comment> findByVideoVideoId(int videoId);

    List<Comment> findByUser(User user);

    Set<Comment> findByNoticeNoticeId(int noticeId);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.assignment a " +
            "LEFT JOIN FETCH c.video v " +
            "WHERE a.course.courseId = :courseId OR v.course.courseId = :courseId")
    List<Comment> findByCourseId(@Param("courseId") int courseId);


}
