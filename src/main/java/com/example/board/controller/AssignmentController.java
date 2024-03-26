package com.example.board.controller;

import com.example.board.domain.Assignment;
import com.example.board.domain.Course;
import com.example.board.dto.AssignmentFileDto;
import com.example.board.dto.LoginInfo;
import com.example.board.service.AssignmentService;
import com.example.board.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.swing.plaf.SpinnerUI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;

    private static final String VIDEO_DIRECTORY = "video";

    @GetMapping("/assignmentWriteForm")
    public String assignmentWriteForm(HttpSession httpSession,
                                  Model model,
                                      @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Course course = courseService.getCourse(courseId);
        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo);

        return "assignmentWriteForm";
    }

    @PostMapping("/assignmentWrite")
    public String assignmentWrite(Model model,
                                  @RequestParam("courseId") int courseId,
                                  @RequestParam("title") String title,
                                @RequestParam("content") String content,
                                  HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        assignmentService.addAssignment(courseId, loginInfo.getUserId(), title, content);
        return "redirect:/assignmentList?currentCourseId=" + courseId;
    }

    @GetMapping("/assignmentFileWriteForm")
    public String assignmentFileWriteForm(HttpSession httpSession,
                                      Model model,
                                      @RequestParam("assignmentId") int assignmentId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        model.addAttribute("assignment", assignment);
        model.addAttribute("loginInfo", loginInfo);
        return "assignmentFileWriteForm";
    }

    @GetMapping("/assignment")
    public  String assignment(@RequestParam("assignmentId") int assignmentId,
                              Model model,
                              HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo);

        Assignment assignment = assignmentService.getAssignment(assignmentId);
        model.addAttribute("assignment", assignment);
        return "assignment";
    }

    @PostMapping("/assignmentFileWrite")
    public String assignmentFileWrite(@RequestParam("file")MultipartFile file,
                                  @RequestParam("assignmentId") int assignmentId,
                                  @RequestParam("userId") int userId) {
        try {
            assignmentService.saveFile(file, assignmentId, userId);
        }catch (IOException e){
            e.printStackTrace();
        }
        return "redirect:/assignmentList?assignmentId=" + assignmentId;
    }

    @GetMapping("/assignmentList")
    public String assignmentList(HttpSession httpSession,
                                 Model model,
                                 @RequestParam("currentCourseId") Integer currentCourseId,
                                 @RequestParam(name = "page", defaultValue = "0") int page){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        model.addAttribute("loginInfo",loginInfo);
        Course course = courseService.getCourse(currentCourseId);
        model.addAttribute("course", course);
        long total = assignmentService.getTotalCount();
        List<Assignment> list = assignmentService.getAssignments(page);
        long pageCount = total / 10;
        if(total % 10 > 0){
            pageCount++;
        }
        int currentPage = page;
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount); // 총 페이지 수를 나타내는것 같다.
        model.addAttribute("currentPage",currentPage); // 현재 페이지 번호

        return "assignmentList";

    }

}
