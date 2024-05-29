package com.example.board.controller;

import com.example.board.domain.Course;
import com.example.board.domain.Notice;
import com.example.board.domain.User;
import com.example.board.dto.LoginInfo;
import com.example.board.service.CourseService;
import com.example.board.service.NoticeService;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final CourseService courseService;
    private final NoticeService noticeService;

    private final UserService userService;

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
        return "redirect:/noticeList?currentCourseId=" + courseId;
    }
}
