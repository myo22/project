package com.example.board.controller;

import com.example.board.domain.Assignment;
import com.example.board.domain.AssignmentFile;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import com.example.board.dto.AssignmentFileDto;
import com.example.board.dto.FileDto;
import com.example.board.dto.LoginInfo;
import com.example.board.service.AssignmentService;
import com.example.board.service.CourseService;
import com.example.board.service.UserService;
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
import java.util.Set;

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
                                  @RequestParam("score") int maxScore,
                                  HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        assignmentService.addAssignment(courseId, loginInfo.getUserId(), maxScore,title, content);
        return "redirect:/assignmentList?currentCourseId=" + courseId;
    }

    @GetMapping("/assignment")
    public  String assignment(@RequestParam("assignmentId") int assignmentId,
                              Model model,
                              HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        Assignment assignment = assignmentService.getAssignment(assignmentId);

        if(assignment.getCourse().getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }else{
            model.addAttribute("isParticipant", true);
        }

        AssignmentFileDto assignmentFile = assignmentService.getFile(assignmentId);

        model.addAttribute("assignmentFile", assignmentFile);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("assignment", assignment);
        return "assignment";
    }

    @GetMapping("/assignmentUpdateForm")
    public String assignmentUpdateForm(HttpSession httpSession,
                                       Model model,
                                       @RequestParam("assignmentId") int assignmentId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        model.addAttribute("assignment", assignment);
        model.addAttribute("loginInfo", loginInfo);
        return "assignmentUpdateForm";
    }

    @PostMapping("/updateAssignment")
    public String updateAssignment(HttpSession httpSession,
                                       Model model,
                                       @RequestParam("assignmentId") int assignmentId,
                                       @RequestParam("title") String title,
                                       @RequestParam("content") String content,
                                       @RequestParam("score") int maxScore){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        model.addAttribute("assignmentId", assignmentId);
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        if(assignment.getUser().getUserId() != loginInfo.getUserId()){
            return "redirect:/assignment?assignmentId=" + assignmentId;
        }
        assignmentService.updateAssignment(assignmentId, title, content, maxScore);
        return "redirect:/assignment?assignmentId=" + assignmentId;
    }

    @GetMapping("/deleteAssignment")
    public String deleteAssignment(@RequestParam("assignmentId") int assignmentId,
                                  HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            assignmentService.deleteAssignment(assignmentId);
        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        return "redirect:/assignmentList?currentCourseId=" + assignment.getCourse().getCourseId();
    }

    @GetMapping("/assignmentFileWriteForm")
    public String assignmentFileWriteForm(HttpSession httpSession,
                                      Model model,
                                      @RequestParam("assignmentId") int assignmentId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        model.addAttribute("loginInfo", loginInfo);
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        model.addAttribute("assignment", assignment);


        return "assignmentFileWriteForm";
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
        return "redirect:/assignment?assignmentId=" + assignmentId;
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

        long total = assignmentService.getTotalCount();
        List<Assignment> list = assignmentService.getAssignments(page);
        long pageCount = total / 10;
        if(total % 10 > 0){
            pageCount++;
        }
        int currentPage = page;

        Course course = courseService.getCourse(currentCourseId);
        if(course.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("loginInfo",loginInfo);
        model.addAttribute("course", course);
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount); // 총 페이지 수를 나타내는것 같다.
        model.addAttribute("currentPage",currentPage); // 현재 페이지 번호

        return "assignmentList";

    }

    @GetMapping("/Assignment/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") int fileId) throws IOException {
        AssignmentFileDto assignmentFileDto = assignmentService.getFile(fileId);
        Path path = Paths.get(assignmentFileDto.getAssignmentPath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + assignmentFileDto.getOrigFilename() + "\"")
                .body(resource);
    }



}
