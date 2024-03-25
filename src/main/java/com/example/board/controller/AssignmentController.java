package com.example.board.controller;

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

    @GetMapping("/assignmentWrite")
    public String assignmentWrite(){
        return "assignmentWrite";
    }

    @GetMapping("/assignmentFileWriteForm")
    public String assignmentFileWriteForm(HttpSession httpSession,
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

    @PostMapping("/assignmentFileWrite")
    public String assignmentFileWrite(@RequestParam("file")MultipartFile file,
                                  @RequestParam("courseId") int courseId,
                                  @RequestParam("usserId") int userId) {
        try {
            assignmentService.saveFile(file, courseId, userId);
        }catch (IOException e){
            e.printStackTrace();
        }
        return "redirect:/assignmentList?currentCourseId=" + courseId;
    }

    @GetMapping("/assignmentList")
    public String assignmentList(HttpSession httpSession, Model model, @RequestParam("currentCourseId") Integer currentCourseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        model.addAttribute("loginInfo",loginInfo);
        Course course = courseService.getCourse(currentCourseId);
        model.addAttribute("course", course);

        List<AssignmentFileDto> fileList = assignmentService.getAllFiles();
        model.addAttribute("fileList", fileList);

        return "assignmentList";

    }

    @GetMapping("/download/{fileId}")
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
