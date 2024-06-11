package com.example.board.controller;

import com.example.board.domain.Comment;
import com.example.board.domain.Course;
import com.example.board.domain.Notice;
import com.example.board.domain.User;
import com.example.board.dto.LoginInfo;
import com.example.board.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final CourseService courseService;
    private final NoticeService noticeService;
    private final UserService userService;
    private final ProgressService progressService;
    private final CommentService commentService;

    @GetMapping("/noticeList")
    public String noticeList(@RequestParam("currentCourseId") int courseId,
            HttpSession httpSession, Model model){

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Course course = courseService.getCourse(courseId);
        List<Notice> notices = noticeService.getNotices(course);

        if(course.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }

        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("course", course);
        model.addAttribute("notices", notices);

        return "noticeList";
    }

    @GetMapping("/noticeWriteForm")
    public String noticeWriteForm(HttpSession httpSession, Model model,
                                  @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId);

        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("course", course);
        return "noticeWriteForm";
    }

    @PostMapping("/writeNotice")
    public String writeNotice(@RequestParam("title") String title,
                              @RequestParam("content") String content,
                              @RequestParam("courseId") int courseId,
                              HttpSession httpSession){

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        User user = userService.getUser(loginInfo.getUserId());
        Course course = courseService.getCourse(courseId);

        noticeService.addNotice(title, content, course, user);
        progressService.incrementNoticeCount(courseId);

        return "redirect:/noticeList?currentCourseId=" + courseId;
    }

    @GetMapping("/notice")
    public String notice(@RequestParam("noticeId") int noticeId,
                         HttpSession httpSession,
                         Model model){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginForm";
        }

        Notice notice = noticeService.getNotice(noticeId);
        Course course = courseService.getCourse(notice.getCourse().getCourseId());

        progressService.watchNotices(course.getCourseId(), loginInfo.getUserId());

        if(notice.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }

        Set<Comment> comments = commentService.getCommentByNotice(noticeId);

        model.addAttribute("comments", comments);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("course", course);
        model.addAttribute("notice", notice);

        return "notice";
    }

    @GetMapping("/noticeUpdateForm")
    public String noticeUpdateForm(@RequestParam("courseId") int courseId,
                                   @RequestParam("noticeId") int noticeId,
                                   HttpSession httpSession,
                                   Model model){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId);
        Notice notice = noticeService.getNotice(noticeId);

        model.addAttribute("longinInfo", loginInfo);
        model.addAttribute("notice", notice);
        model.addAttribute("course", course);
        return "noticeUpdateForm";
    }

    @PostMapping("/updateNotice")
    public String updateNotice(@RequestParam("noticeId") int noticeId,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("courseId") int courseId,
                               HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginForm";
        }

        Notice notice = noticeService.getNotice(noticeId);
        noticeService.updateNotice(title, content, notice);
        return "redirect:/notice?noticeId=" + noticeId +"&courseId=" + courseId;

    }

    @GetMapping("/deleteNotice")
    public String deleteNotice(@RequestParam("noticeId") int noticeId,
                               @RequestParam("courseId") int courseId,
                               HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginForm";
        }

        noticeService.deleteNotice(noticeId);

        return "redirect:/noticeList?currentCourseId=" + courseId;
    }
}
