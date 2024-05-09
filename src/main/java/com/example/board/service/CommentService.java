package com.example.board.service;

import com.example.board.Repository.*;
import com.example.board.domain.*;
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
                return commentRepository.save(new Comment(content, user, null, video));
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

    @Transactional
    public List<String> analyzeComments(int courseId){

        // 전체 모델보단 정확도는 떨어지지만 처리 속도가 빠른 경량 모델을 사용한다.
        Komoran komoran = new Komoran("model-light");

        List<Comment> comments = commentRepository.findAll();

        // 빈도수 계산한 후 많이 등장하는 형태소를 포함한 댓글을 저장할 곳.
        Map<String, Set<String>> morphToCommentsMap = new HashMap<>();

        for (Comment comment : comments){
            String cleanedComment = comment.getContent().trim().replaceAll("\\s+", " ").toLowerCase();

            // 댓글의 형태소를 분석하고, 토큰 리스트를 가져오는 것.
            List<Token> tokens = komoran.analyze(cleanedComment).getTokenList();

            for (Token token : tokens){
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

}

