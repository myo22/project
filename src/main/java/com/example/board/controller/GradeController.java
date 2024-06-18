package com.example.board.controller;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class GradeController {

    private final CourseService courseService;
    private final GradeService gradeService;


    @GetMapping("/gradeForm")
    public String gradeForm(@RequestParam("currentCourseId") int courseId,
                            Model model,
                            HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

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

        if(loginInfo != null && course.getUser() != null && loginInfo.getUserId() == course.getUser().getUserId()){
            model.addAttribute("isAdmin", true);
        }

        model.addAttribute("course", course);
        model.addAttribute("participants", participantGrades);
        model.addAttribute("loginInfo", loginInfo);
        return "gradeForm";

    }


    @PostMapping("/confirmGrade")
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
