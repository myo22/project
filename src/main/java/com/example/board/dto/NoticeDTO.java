package com.example.board.dto;

import com.example.board.domain.Notice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeDTO {
    private int noticeId;
    private String title;
    private String content;
    private int courseId;
    private int userId;

    public NoticeDTO(Notice notice){
        this.noticeId = notice.getNoticeId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.courseId = notice.getCourse().getCourseId();
        this.userId = notice.getUser().getUserId();

    }
}
