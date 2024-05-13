package com.example.board.controller;

import com.example.board.domain.Comment;
import com.example.board.domain.Course;
import com.example.board.domain.Grade;
import com.example.board.domain.User;
import com.example.board.dto.LoginInfo;
import com.example.board.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final GradeService gradeService;
    private final CommentService commentService;
    private final NotificationService notificationService;

    @GetMapping("/")
    public String Courselist(HttpSession httpSession, Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null){
            return "redirect:/loginForm";
        }

        long total = courseService.getTotalCount();
        List<Course> list = courseService.getCourses(page);
        long pageCount = total / 10;
        if (total % 10 > 0) {
            pageCount++;
        }
        int currentPage = page;


        User user = userService.getUser(loginInfo.getUserId());
        Set<Course> courses =  user.getCourses();

        List<String> importantComments = new ArrayList<>();
        for(Course course : courses){
            List<Comment> comments = commentService.getCommentByCourse(course);
            List<String> commentTexts = new ArrayList<>();
            for (Comment comment : comments) {
                commentTexts.add(comment.getContent());
            }
            // TF-IDF를 사용하여 댓글 벡터화
            Map<String, Map<String, Double>> tfidfMatrix = commentService.calculateTFIDF(commentTexts);
            // 댓글 간 유사도 계산
            Map<String, Map<String, Double>> commentSimilarities = commentService.calculateCommentSimilarities(tfidfMatrix);
            // 개선된 주요 댓글 추출
            List<String> importantCommentsForCourse = commentService.extractImportantComments(commentSimilarities, 2);
            importantComments.addAll(importantCommentsForCourse);
//            // 사용자-댓글 행렬 구성
//            Map<String, Map<String, Double>> userCommentMatrix = commentService.constructUserCommentMatrix(users, comments);
//            // 사용자 간의 유사도 계산
//            Map<String, Map<String, Double>> userSimilarities = commentService.calculateUserSimilarities(userCommentMatrix);
//            // 개선된 주요 댓글 추출
//            List<String> improvedImportantComments = commentService.extractImprovedImportantComments(userSimilarities, commentSimilarities, 2);

        }

        model.addAttribute("importantComments", importantComments);
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

        Set<User> participants = course.getParticipants();
        Map<User, Grade> participantGrades = new HashMap<>();
        for(User participant : participants){
            if(participant.getCourses().contains(course)){
                // 어차피 여기서 강의를 가지고 있는지 확인하니까 User만 보내도 해당 강의에 있는 사람을 자동으로 검색해주는거 아닌가?
                Grade grade = gradeService.calculateGrade(participant, course);
                participantGrades.put(participant, grade);
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("participants", participantGrades);
        model.addAttribute("loginInfo", loginInfo);
        return "participantList";
    }

    @PostMapping("confirmGrade")
    public String confirmGrade(@RequestParam("courseId") int courseId,
                               HttpServletRequest request){
        Course course = courseService.getCourse(courseId);
        Set<User> participants = course.getParticipants();

        Map<String, String[]> parameters = request.getParameterMap();
        for(User participant : participants){
            String key = "gradeLetter-" + participant.getUserId();
            if(parameters.containsKey(key)){
                String gradeLetter = request.getParameter(key);
                Grade grade = gradeService.findGradeByUserAndCourse(participant, course);
                if (grade == null) {
                    grade = new Grade();
                    grade.setUser(participant);
                    grade.setCourse(course);
                }
                grade.setGradeLetter(gradeLetter);
                gradeService.saveGrade(grade);
            }else {
                Grade grade = gradeService.calculateGrade(participant, course);
                gradeService.saveGrade(grade);
            }
        }
        return "redirect:/participantList?currentCourseId=" + courseId;
    }

}
