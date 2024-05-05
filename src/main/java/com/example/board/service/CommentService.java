package com.example.board.service;

import com.example.board.Repository.AssignmentRepository;
import com.example.board.Repository.CommentRepository;
import com.example.board.Repository.VideoRepository;
import com.example.board.domain.Assignment;
import com.example.board.domain.Comment;
import com.example.board.domain.User;
import com.example.board.domain.Video;
import com.example.board.dto.VideoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AssignmentRepository assignmentRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public Comment addCommentToAssignment(int assignmentId, String content, User user){
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .assignment(assignment)
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment addCommentToVideo(int videoId, String content, User user){
        Video video = videoRepository.findById(videoId).orElseThrow();
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .video(video)
                .build();
        return commentRepository.save(comment);
    }
}
