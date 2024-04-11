package com.example.board.controller;

import com.example.board.domain.Course;
import com.example.board.domain.User;
import com.example.board.dto.LoginInfo;
import com.example.board.service.CourseService;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/")
    public String Courselist(HttpSession httpSession, Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo);

        long total = courseService.getTotalCount();
        List<Course> list = courseService.getCourses(page);
        long pageCount = total / 10;
        if (total % 10 > 0) {
            pageCount++;
        }
        int currentPage = page;


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

    @GetMapping("/coursewriteForm")
    public String coursewriteForm(HttpSession httpSession, Model model) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        model.addAttribute("loginInfo", loginInfo);

        return "coursewriteForm";
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

        List<String> roles = loginInfo.getRoles();
        if (roles.contains("ROLE_ADMIN")) {
            courseService.deleteCourse(courseId);
        } else {
            courseService.deleteCourse(loginInfo.getUserId(), courseId);
        }

        return "redirect:/";
    }

    @GetMapping("/courseupdateform")
    public String courseupdateform(@RequestParam("courseId") int courseId,
                             Model model,
                             HttpSession httpSession) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId, false);
        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo);
        return "courseupdateform";
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

        Course course = courseService.getCourse(courseId, false);
        if (course.getUser().getUserId() != loginInfo.getUserId()) {
            return "redirect:/course?courseId=" + courseId;
        }

        courseService.updateCourse(courseId, title, content);
        return "redirect:/course?courseId=" + courseId;
    }

    @PostMapping ("/courses")
    public String joinCourse(@RequestParam("courseId") int courseId,
                             HttpSession httpSession) {

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId, false);
        User user = userService.getUser(loginInfo.getUserId());
        Set<User> participants = course.getParticipants();
        if(course.getUser().getUserId() == loginInfo.getUserId() || participants.contains(user)){
            return "redirect:/";
        }
        // 사용자가 강좌에 참가하도록 Service에 요청합니다.
        courseService.joinCourse(courseId, loginInfo.getUserId());
        for (User participant : participants) {
            System.out.println("Participant: " + participant.getName());
        }
        // 강좌 참가가 완료되면 다시 해당 강좌 페이지로 리다이렉트합니다.
        return "redirect:/course?courseId=" + courseId;
    }

    @GetMapping("/participantList")
    public String participantList(Model model,
                                  HttpSession httpSession,
                                  @RequestParam("currentCourseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("LoginInfo");
        Course course = courseService.getCourse(courseId);
        Set<User> enrolledParticipants = new HashSet<>();
        Set<User> participants = course.getParticipants();
        for(User participant : participants){
            if(participant.getCourses().contains(course)){
                enrolledParticipants.add(participant);
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("participants", enrolledParticipants);
        model.addAttribute("loginInfo", loginInfo);
        return "participantList";
    }
}
