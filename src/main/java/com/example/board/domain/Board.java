package com.example.board.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

// @Entity(name = "Board") 이렇게 적어줘야하는데 안적으면 클래스 이름이 엔티티 이름이 된다.
@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Board extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false) // 칼럼의 길이와 null허용여부
    private String title;

    @Column(length = 2000, nullable = false)
    private String content; // text type

    @Column(name = "view_cnt")
    private Integer viewCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY) // 게시물 N --- 1 사용자, FetchType.EAGER(안써줘도 기본값이다) -> 무조건 데이터를 가지고와라.
    @JoinColumn(name = "user_id")
    private User user;

//    @Override
//    public String toString() {
//        return "Board{" +
//                "boardId=" + boardId +
//                ", title='" + title + '\'' +
//                ", content='" + content + '\'' +
//                ", regdate=" + regdate +
//                ", viewCnt=" + viewCnt +
//                '}';
//    }

    public void changeView(int viewCnt){
        this.viewCnt = viewCnt;
    }

    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }

    // 커스텀 매퍼에서만 접근할 수 있도록 protected로 변경
    protected void setCourse(Course course){
        this.course = course;
    }

    protected void setUser(User user){
        this.user = user;
    }

}

//'board_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
//'title', 'varchar(100)', 'NO', '', NULL, ''
//'content', 'text', 'YES', '', NULL, ''
//'user_id', 'int', 'NO', 'MUL', NULL, ''
//'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
//'view_cnt', 'int', 'YES', '', '0', ''
