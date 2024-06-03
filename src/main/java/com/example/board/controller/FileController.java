package com.example.board.controller;

import com.example.board.domain.Course;
import com.example.board.domain.User;
import com.example.board.dto.FileDto;
import com.example.board.dto.LoginInfo;
import com.example.board.service.CourseService;
import com.example.board.service.FileService;
import com.example.board.service.ProgressService;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;


@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final CourseService courseService;
    private final UserService userService;
    private final ProgressService progressService;

    private static final String VIDEO_DIRECTORY = "video";

    @GetMapping("/studyHubWriteForm")
    public String studyhub(HttpSession httpSession,
                           Model model,
                           @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Course course = courseService.getCourse(courseId);
        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo);
        return "studyHubWriteForm";
    }

    @PostMapping("/studyHubWrite")
    public String studyhubwrite(@RequestParam("file") MultipartFile file,
                                @RequestParam("courseId") int courseId) {
        try {
            // 파일 업로드 및 파일 정보 저장
            fileService.saveFile(file, courseId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressService.incrementResourceCount(courseId);
        return "redirect:/studyHubList?currentCourseId=" + courseId;
    }

    @GetMapping("/File/delete/{fileId}")
    public String fileDelete(@PathVariable("fileId") int fileId) {
        // 파일 삭제 기능 구현
        fileService.deleteFile(fileId);
        return "redirect:/studyHubList"; // 파일 삭제 후 목록 페이지로 리다이렉트
    }

    @GetMapping("/studyHubList")
    public String studyhublist(HttpSession httpSession, Model model, @RequestParam("currentCourseId") Integer currentCourseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Course course = courseService.getCourse(currentCourseId);
        List<FileDto> fileList = fileService.getAllFiles();

        if(course.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("loginInfo",loginInfo);
        model.addAttribute("course", course);
        model.addAttribute("fileList", fileList);

        return "studyHubList";
    }

    @GetMapping("/File/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") int fileId) throws IOException {
        FileDto fileDto = fileService.getFile(fileId);
        Path path = Paths.get(fileDto.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getOrigFilename() + "\"")
                .body(resource);
    }
}