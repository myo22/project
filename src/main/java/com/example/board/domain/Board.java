package com.example.board.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    // @OneToMany는 기본적으로 각 엔티티에 해당하는 테이블을 독립적으로 생성하고 중간에 매핑해 주는 테이블이 생성된다.
    @OneToMany(mappedBy = "board",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY) // BoardImage의 board변수
    @Builder.Default
    private Set<BoardImage> imageSet = new HashSet<>();

    //상위 엔티티가 하위 엔티티 객체들을 관리하는 경우에는 별도의 JPARepository를 생성하지 않고, Board 엔티티에 하위 엔티티 객체들을 관리하는 기능을 추가해서 사용합니다.
    public void addImage(String uuid, String fileName){

        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImages(){
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();
    }

    public void changeView(int viewCnt){
        this.viewCnt = viewCnt;
    }

    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }

    // 커스텀 매퍼에서만 접근할 수 있도록 public로 변경
    public void setCourse(Course course){
        this.course = course;
    }

    public void setUser(User user){
        this.user = user;
    }

}

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


//'board_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
//'title', 'varchar(100)', 'NO', '', NULL, ''
//'content', 'text', 'YES', '', NULL, ''
//'user_id', 'int', 'NO', 'MUL', NULL, ''
//'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
//'view_cnt', 'int', 'YES', '', '0', ''
