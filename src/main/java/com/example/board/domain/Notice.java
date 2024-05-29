package com.example.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Integer noticeId;

    @Column(length = 100)
    private String title;

    @Lob
    private String content;

    @CreationTimestamp
    private LocalDateTime regdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;


    @Builder
    public Notice(String title, String content, LocalDateTime regdate ,Course course, User user){
        this.title = title;
        this.content = content;
        this.course = course;
        this.user = user;
        this.regdate = regdate;
    }
}
