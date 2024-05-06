package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 여기서 nullable = true는 굳이 안써도 되는 기본값이다. 유연한 데이터베이스를 위한 것.
    @ManyToOne
    @JoinColumn(name = "video_id", nullable = true)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = true)
    private Assignment assignment;

    @Builder
    public Comment(String content, User user, Assignment  assignment,  Video video){
        this.content = content;
        this.user = user;
        this.assignment = assignment;
        this.video = video;
    }


}
