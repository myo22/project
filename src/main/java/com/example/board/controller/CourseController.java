package com.example.board.controller;

import com.example.board.domain.*;
import com.example.board.dto.LoginInfo;
import com.example.board.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final CommentService commentService;
    private final NoticeService noticeService;
    private final ProgressService progressService;

    @GetMapping("/")
    public String courseList(HttpSession httpSession, Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null){
            return "redirect:loginForm";
        }

        long total = courseService.getTotalCount();
        List<Course> list = courseService.getCourses(page);
        long pageCount = total / 10;
        if (total % 10 > 0) {
            pageCount++;
        }
        int currentPage = page;

        User user = userService.getUser(loginInfo.getUserId());

        // 메모리 기반 댓글 추천
        List<String> recommendedCosineComments = commentService.recommendCosineComments(user, 3);
        List<String> recommendPearsonComments = commentService.recommendPearsonComments(user, 3);
        List<String> recommendJaccardComments = commentService.recommendJaccardForUser(user, 3);

        // 모델 기반 댓글 추천
        Map<String, List<String>> recommendedModelComments = commentService.recommendModelComments(user, 3);

        Set<Progress> progresses = progressService.getProgresses(user.getUserId());
        Set<Course> courses = user.getCourses();

        if(courses != null){
            model.addAttribute("courses",user.getCourses());
        }

        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            List<Course> courseList = courseService.getUserCourses(user);
            model.addAttribute("professor", true);
            model.addAttribute("courseList", courseList);
            model.addAttribute("courseCount", courseList.size());
        }else{
            model.addAttribute("student", true);
            model.addAttribute("courseCount", progresses.size());
        }

        model.addAttribute("progresses", progresses);
        model.addAttribute("recommendedCosineComments", recommendedCosineComments);
        model.addAttribute("recommendPearsonComments", recommendPearsonComments);
        model.addAttribute("recommendJaccardComments", recommendJaccardComments);
        model.addAttribute("recommendedModelComments", recommendedModelComments);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);

        return "courselist";
    }

    @GetMapping("/course")
    public String course(@RequestParam("courseId") int courseId, Model model, HttpSession httpSession) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }

        model.addAttribute("loginInfo", loginInfo);
        Course course = courseService.getCourse(courseId);
        model.addAttribute("course", course);
        List<Notice> notices = noticeService.getNotices(course);
        model.addAttribute("notices", notices);

        User user = userService.getUser(loginInfo.getUserId());
        Set<User> participants =  course.getParticipants();
        if(loginInfo.getUserId() == course.getUser().getUserId()){
            model.addAttribute("isAdmin", true);
            return "course";
        }else if(participants.contains(user)){
            model.addAttribute("isParticipant", true);
            return "course";
        }

        return "redirect:/";
    }

    @GetMapping("/coursePlan")
    public String coursePlan(@RequestParam("currentCourseId") int courseId, Model model, HttpSession httpSession) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }

        model.addAttribute("loginInfo", loginInfo);
        Course course = courseService.getCourse(courseId);
        model.addAttribute("course", course);

        User user = userService.getUser(loginInfo.getUserId());
        Set<User> participants =  course.getParticipants();
        if(loginInfo.getUserId() == course.getUser().getUserId()){
            model.addAttribute("isAdmin", true);
            return "coursePlan";
        }else if(participants.contains(user)){
            model.addAttribute("isParticipant", true);
            return "coursePlan";
        }

        return "redirect:/";
    }

    @GetMapping("/courseWriteForm")
    public String coursewriteForm(HttpSession httpSession, Model model) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        model.addAttribute("loginInfo", loginInfo);

        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            return "courseWriteForm";
        }

        return "redirect:/";
    }

    @PostMapping("/writeCourse")
    public String writeCourse(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession httpSession
    ) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        courseService.addCourse(loginInfo.getUserId(), title, content);
        return "redirect:/";
    }

    @GetMapping("/deleteCourse")
    public String deleteCourse(
            @RequestParam("courseId") int courseId,
            HttpSession httpSession
    ) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        // 이것도 마찬가지로 교수면 다 지워지는게 아니라 해당 강의를 개설한 교수만 가능한거다.
//        List<String> roles = loginInfo.getRoles();
//        if (roles.contains("ROLE_ADMIN")) {
//            courseService.deleteCourse(courseId);
//        } else {
//            courseService.deleteCourse(loginInfo.getUserId(), courseId);
//        }

        Course course = courseService.getCourse(courseId);
        if(course.getUser().getUserId() == loginInfo.getUserId()){
            courseService.deleteCourse(courseId);
        }

        return "redirect:/";
    }

    @GetMapping("/courseUpdateForm")
    public String courseUpdateForm(@RequestParam("courseId") int courseId,
                             Model model,
                             HttpSession httpSession) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId);
        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo);
        return "courseUpdateForm";
    }

    @PostMapping("/updateCourse")
    public String updateCourse(@RequestParam("courseId") int courseId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession httpSession) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId);
        if (course.getUser().getUserId() != loginInfo.getUserId()) {
            return "redirect:/course?courseId=" + courseId;
        }

        courseService.updateCourse(courseId, title, content);
        return "redirect:/course?courseId=" + courseId;
    }

    @GetMapping ("/courses")
    public String courses(@RequestParam("courseId") int courseId,
                             HttpSession httpSession) {

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId);
        User user = userService.getUser(loginInfo.getUserId());
        Set<User> participants = course.getParticipants();

        if(course.getUser().getUserId() == user.getUserId() || participants.contains(user)){
            return "redirect:/";
        }
        // 사용자가 강좌에 참가하도록 Service에 요청합니다.
        courseService.joinCourse(courseId, user.getUserId());
        progressService.AddProgress(courseId, user.getUserId());
        for (User participant : participants) {
            System.out.println("Participant: " + participant.getName());
        }
        // 강좌 참가가 완료되면 다시 해당 강좌 페이지로 리다이렉트합니다.
        return "redirect:/course?courseId=" + courseId;
    }


}
