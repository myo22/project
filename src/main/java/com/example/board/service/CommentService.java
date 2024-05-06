package com.example.board.service;

import com.example.board.Repository.*;
import com.example.board.domain.*;
import com.example.board.dto.CommentDTO;
import com.example.board.dto.VideoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AssignmentRepository assignmentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

//    @Transactional
//    public Comment addCommentToAssignment(int assignmentId, String content, User user){
//        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
//        Comment comment = Comment.builder()
//                .content(content)
//                .user(user)
//                .assignment(assignment)
//                .build();
//        return commentRepository.save(comment);
//    }
//
//    @Transactional
//    public Comment addCommentToVideo(int videoId, String content, User user){
//        Video video = videoRepository.findById(videoId).orElseThrow();
//        Comment comment = Comment.builder()
//                .content(content)
//                .user(user)
//                .video(video)
//                .build();
//        return commentRepository.save(comment);
//    }

    @Transactional
    public Comment addComment(int commentId, String content, User user, String contentType){
        switch (contentType){
            case "assignment":
                Assignment assignment = assignmentRepository.findById(commentId).orElseThrow();
                return commentRepository.save(new Comment(content, user, assignment, null));
            case "video":
                Video video = videoRepository.findById(commentId).orElseThrow();
                commentRepository.save(new Comment(content, user, null, video));
            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }

    @Transactional
    public List<Comment> getCommentsByAssignment(Assignment assignment){

        return commentRepository.findByAssignment(assignment);
    }

    @Transactional
    public List<Comment> getCommentByVideo(Video video){
        return commentRepository.findByVideo(video);
    }


//    public Comment toComment(CommentDTO commentDTO){
//        User user = userRepository.findById(commentDTO.getUserId()).orElseThrow();
//        Assignment assignment = assignmentRepository.findById(commentDTO.getAssignmentId()).orElseThrow();
//        Video video = videoRepository.findById(commentDTO.getVideoId()).orElseThrow();
//        Comment comment = new Comment();
//        comment.setCommentId(commentDTO.getCommentId());
//        comment.setContent(commentDTO.getContent());
//        comment.setUser(user);
//        comment.setCreatedAt(commentDTO.getCreatedAt());
//        comment.setAssignment(assignment);
//        comment.setVideo(video);
//        return comment;
//    }
}

