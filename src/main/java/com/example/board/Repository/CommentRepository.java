package com.example.board.Repository;

import com.example.board.domain.Assignment;
import com.example.board.domain.Comment;
import com.example.board.domain.User;
import com.example.board.domain.Video;
import com.example.board.dto.CommentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByAssignment(Assignment assignment);

    List<Comment> findByVideo(Video video);

    List<Comment> findByUser(User user);

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.assignment a " +
            "LEFT JOIN FETCH c.video v " +
            "WHERE a.course.courseId = :courseId OR v.course.courseId = :courseId")
    List<Comment> findByCourseId(@Param("courseId") int courseId);


}
