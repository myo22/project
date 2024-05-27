package com.example.board.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity // Database Table과 맵핑하는 객체.
@Table(name = "user") // Database 테이블 이름이 user2와 User라는 객체가 맵핑.
@NoArgsConstructor
//@ToString -> 밑에 roles까지 ToString으로 만들기위해서 select문이 자동으로 실행되기 때문에 재귀호출 문제가 발생한다.
@Getter
@Setter
public class User {

    @Id // 이 필드가 Table의 PK.
    @Column(name = "user_id") // userId 필드는 user_id와 관련이 있다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // userId는 자동으로 생성되도록 하는 것이다. 1.2.3.4... = AutoIncrement
    private Integer userId;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String name;

    @Column(length = 500)
    private String password;

    @CreationTimestamp // 현재시간이 저장될 때 자동으로 생성.
    private LocalDateTime regdate;

    @ManyToMany
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    ) // 이렇게 적어줘야 User와 Role을 관계로 맺어주는 것이다.
    Set<Role> roles = new HashSet<>();

    // 내가 참여한 강의들이다.
    @ManyToMany(mappedBy = "participants")
    private Set<Course> courses = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AssignmentFile> assignmentFiles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Grade> grades = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attendance> attendances = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Video> videos = new HashSet<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Assignment> assignments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Board> boards = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notice> notices = new HashSet<>();

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", regdate=" + regdate +
                '}';
    }
}

// User ------> Role N:N관계이지만 Role에서는 User를 가지고오지 못하는 단반향 관계이다.