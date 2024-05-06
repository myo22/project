package com.example.board.dto;

import com.example.board.domain.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDTO {
    private int commentId;

    private String content;

    private int userId;
    private LocalDateTime createdAt;;
    private int videoId;
    private int assignmentId;

    public CommentDTO(Comment comment){
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getUserId();
        this.createdAt = comment.getCreatedAt();
        this.videoId = comment.getVideo().getVideoId();
        this.assignmentId = comment.getAssignment().getAssignmentId();
    }

}
