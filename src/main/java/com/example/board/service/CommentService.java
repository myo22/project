package com.example.board.service;

import com.example.board.Repository.*;
import com.example.board.domain.*;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import lombok.RequiredArgsConstructor;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AssignmentRepository assignmentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


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
    public Comment addComment(int contentId, String content, User user, String contentType) {
        switch (contentType) {
            case "assignment":
                Assignment assignment = assignmentRepository.findById(contentId).orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
                List<String> assignmentComments = analyzeComments(assignment);
                for (String comment : assignmentComments) {
                    notificationService.dispatch(assignment.getUser().getUserId(), comment);
                }
                return commentRepository.save(new Comment(content, user, assignment, null));
            case "video":
                Video video = videoRepository.findById(contentId).orElseThrow(() -> new IllegalArgumentException("Video not found"));
                List<String> videoComments = analyzeComments(video);
                for (String comment : videoComments) {
                    notificationService.dispatch(video.getUser().getUserId(), comment);
                }
                return commentRepository.save(new Comment(content, user, null, video));
            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }

    public List<String> analyzeComments(Assignment assignment) {
        List<Comment> comments = commentRepository.findByAssignment(assignment);
        return extractImportantComments(comments);
    }

    public List<String> analyzeComments(Video video) {
        List<Comment> comments = commentRepository.findByVideo(video);
        return extractImportantComments(comments);
    }

    public List<String> analyzeComments(Course course){
        List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
        return extractImportantComments(comments);
    }

    private List<String> extractImportantComments(List<Comment> comments) {
        Map<String, Set<String>> morphToCommentsMap = new HashMap<>();
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        for (Comment comment : comments) {
            String cleanedComment = comment.getContent().trim().replaceAll("\\s+", " ").toLowerCase();
            List<Token> tokens = komoran.analyze(cleanedComment).getTokenList();
            for (Token token : tokens) {
                String morph = token.getMorph();
                morphToCommentsMap.computeIfAbsent(morph, k -> new HashSet<>()).add(comment.getContent());
            }
        }
        return getTopQuestions(morphToCommentsMap, 3);
    }


    public List<String> getTopQuestions(Map<String, Set<String>> morphToCommentsMap , int topN){
        return morphToCommentsMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(topN)
                .flatMap(e -> e.getValue().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Comment> getCommentsByAssignment(Assignment assignment){

        return commentRepository.findByAssignment(assignment);
    }

    @Transactional
    public List<Comment> getCommentByVideo(Video video){
        return commentRepository.findByVideo(video);
    }

}

