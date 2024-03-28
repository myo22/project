package com.example.board.controller;

import com.example.board.domain.Course;
import com.example.board.dto.LoginInfo;
import com.example.board.service.CourseService;
import com.example.board.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final CourseService courseService;

    @GetMapping("/videoList")
    public String videoList(Model model,
                            HttpSession httpSession,
                            @RequestParam("currentCourseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/longinForm";
        }
        model.addAttribute("loginInfo", loginInfo);
        Course course = courseService.getCourse(courseId);
        model.addAttribute("course", course);
        return "videoList";
    }

}
