package com.example.board.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @Column(length = 100)
    private String title;

    @Lob
    private String content;

    @CreationTimestamp
    private LocalDateTime regdate;

    @ManyToOne(fetch = FetchType.LAZY) // 게시물 N --- 1 사용자, FetchType.EAGER(안써줘도 기본값이다) -> 무조건 데이터를 가지고와라.
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "courses")
    private Set<User> participants = new HashSet<>();

}
