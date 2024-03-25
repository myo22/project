package com.example.board.controller;

import com.example.board.domain.Course;
import com.example.board.dto.FileDto;
import com.example.board.dto.LoginInfo;
import com.example.board.service.CourseService;
import com.example.board.service.FileService;
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
public class FileController {

    private final FileService fileService;
    private final CourseService courseService;

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
        return "redirect:/studyHubList?currentCourseId=" + courseId;
    }

    @GetMapping("/studyHubList")
    public String studyhublist(HttpSession httpSession, Model model, @RequestParam("currentCourseId") Integer currentCourseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        model.addAttribute("loginInfo",loginInfo);
        Course course = courseService.getCourse(currentCourseId);
        model.addAttribute("course", course);

        List<FileDto> fileList = fileService.getAllFiles();
        model.addAttribute("fileList", fileList);

        return "studyHubList";
    }

    @GetMapping("/download/{fileId}")
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