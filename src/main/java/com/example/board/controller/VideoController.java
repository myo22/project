package com.example.board.controller;

import com.example.board.domain.Assignment;
import com.example.board.domain.Course;
import com.example.board.domain.Video;
import com.example.board.dto.LoginInfo;
import com.example.board.dto.VideoDTO;
import com.example.board.service.CourseService;
import com.example.board.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import java.util.List;

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
        List<VideoDTO> list = videoService.getAllVideos();
        model.addAttribute("list", list);
        return "videoList";
    }

    @GetMapping("/videoWriteForm")
    public String videoWriteForm(HttpSession httpSession,
                                 Model model,
                                 @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Course course = courseService.getCourse(courseId);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("course", course);
        return "videoWriteForm";
    }

    @PostMapping("/videoWrite")
    public String videoWrite(@RequestParam("video") MultipartFile file,
                             @RequestParam("courseId") int courseId,
                             @RequestParam("title") String title
                             ){
        Course course = courseService.getCourse(courseId);
        try {
            // 파일 업로드 및 파일 정보 저장
            videoService.uploadVideo(file, title, courseId, course.getUser().getUserId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/videoList?currentCourseId=" + courseId;
    }

    @GetMapping("/video")
    public String video(@RequestParam("videoId") int videoId,
                        HttpSession httpSession,
                        Model model){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo);
        VideoDTO video = videoService.getVideo(videoId);
        model.addAttribute("video", video);
        return "video";
    }

    @GetMapping("/videoUpdateForm")
    public String videoUpdateForm(@RequestParam("videoId") int videoId,
                                  HttpSession httpSession,
                                  Model model){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        model.addAttribute("videoId", videoId);
        model.addAttribute("loginInfo", loginInfo);

        return "videoUpdateForm";
    }

    @PostMapping("/videoUpdate")
    public String videoUpdate(@RequestParam("video") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("videoId") int videoId){
        VideoDTO videoDTO = videoService.getVideo(videoId);
        videoDTO.setTitle(title);

        if(!file.isEmpty()){
            videoService.updateVideo(videoId, title ,file);
        }
        return "redirect:/video?videoId=" + videoId;
    }

    @GetMapping("/deleteVideo")
    public String deleteVideo(@RequestParam("videoId") int videoId){
        VideoDTO videoDTO = videoService.getVideo(videoId);
        videoService.deleteVideo(videoId);

        return "redirect:/videoList?currentCourseId=" + videoDTO.getCourseId();
    }

    @GetMapping("/video/stream/{videoId}")
    public ResponseEntity<Resource> streamVideo(@PathVariable int videoId) {

        VideoDTO video = videoService.getVideo(videoId);
        // 동영상 파일의 경로를 생성합니다. (실제로는 파일의 경로가 여기에 와야 합니다)
        String videoFilePath = video.getVideoUrl();
        System.out.println("Video File Path: " + videoFilePath);


        // FileSystemResource를 사용하여 동영상 파일을 로드합니다.
        Resource videoResource = new FileSystemResource(videoFilePath);

        // 동영상을 스트리밍하기 위해 ResponseEntity를 사용합니다.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .body(videoResource);
    }


}
