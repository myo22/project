package com.example.board.Repository;

import com.example.board.domain.Assignment;
import com.example.board.domain.Comment;
import com.example.board.domain.Video;
import com.example.board.dto.CommentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByAssignment(Assignment assignment);

    List<Comment> findByVideo(Video video);

    @Query("select c from Comment c join fetch c.assignment a join fetch a.course where a.course.courseId = :courseId")
    List<Comment> findByCourseId(@Param("courseId") int courseId);


}
