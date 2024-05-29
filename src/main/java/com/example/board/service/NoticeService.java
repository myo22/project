package com.example.board.service;

import com.example.board.Repository.NoticeRepository;
import com.example.board.domain.Course;
import com.example.board.domain.Notice;
import com.example.board.domain.User;
import com.example.board.dto.NoticeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public void addNotice(String title, String content ,Course course, User user){
        Notice notice = Notice.builder()
                .title(title)
                .content(content)
                .course(course)
                .user(user)
                .build();

        noticeRepository.save(notice);
    }

    @Transactional
    public Notice getNotice(int noticeId){
        return noticeRepository.findById(noticeId).orElseThrow();
    }

    @Transactional
    public List<Notice> getNotices(Course course){
        return noticeRepository.findByCourse(course);
    }

}
