package com.example.board.controller;

import com.example.board.domain.Comment;
import com.example.board.domain.Course;
import com.example.board.dto.LoginInfo;
import com.example.board.dto.VideoDTO;
import com.example.board.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final GradeController gradeService;
    private final CommentService commentService;
    private final ProgressService progressService;

    private static final String UPLOAD_DIR = "C:/uploads/";
    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB


    @GetMapping("/videoList")
    public String videoList(Model model,
                            HttpSession httpSession,
                            @RequestParam("currentCourseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/longinForm";
        }
        List<VideoDTO> list = videoService.getAllVideos(loginInfo.getUserId());
        Course course = courseService.getCourse(courseId);

        if(course.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }



        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo);
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
                             @RequestParam("title") String title,
                             HttpSession httpSession
                             ){
        Course course = courseService.getCourse(courseId);
        try {
            // 파일 업로드 및 파일 정보 저장
            videoService.uploadVideo(file, title, courseId, course.getUser().getUserId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressService.incrementVideoCount(courseId);
        return "redirect:/videoList?currentCourseId=" + courseId;
    }

    @GetMapping("/video")
    public String video(@RequestParam("videoId") int videoId,
                        HttpSession httpSession,
                        Model model){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        VideoDTO video = videoService.getVideo(videoId);

        if(video.getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }
        Set<Comment> comments = commentService.getCommentByVideo(videoId);

        Course course = courseService.getCourse(video.getCourseId());

        model.addAttribute("course", course);
        model.addAttribute("comments", comments);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("video", video);
        model.addAttribute("videoId", videoId);
        model.addAttribute("userId", loginInfo.getUserId());
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

        VideoDTO video = videoService.getVideo(videoId);
        Course course = courseService.getCourse(video.getCourseId());

        model.addAttribute("course", course);
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
    public ResponseEntity<Resource> streamVideo(@PathVariable int videoId,
                                                HttpSession httpSession) {

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");

        VideoDTO video = videoService.getVideo(videoId);
        // 동영상 파일의 경로를 생성합니다. (실제로는 파일의 경로가 여기에 와야 합니다)
        String videoFilePath = video.getVideoUrl();
        System.out.println("Video File Path: " + videoFilePath);


        // FileSystemResource를 사용하여 동영상 파일을 로드합니다.
        Resource videoResource = new FileSystemResource(videoFilePath);
        progressService.watchVideos(video.getCourseId(), loginInfo.getUserId());
        attendanceService.recordAttendance(videoId, loginInfo.getUserId());

        // 동영상을 스트리밍하기 위해 ResponseEntity를 사용합니다.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .body(videoResource);
    }

    @PostMapping("/watchVideo")
    public String watchVideo(@RequestParam("videoId") int videoId,
                             HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }


        return "redirect:/video?videoId=" + videoId;
    }

//    @PostMapping("/uploadChunk")
//    public ResponseEntity<String> uploadChunk(@RequestParam("file") MultipartFile fileChunk) {
//        try {
//            // 임시 파일 이름 생성
//            String tempFileName = UUID.randomUUID().toString();
//            File tempFile = new File(UPLOAD_DIR, tempFileName);
//
//            // 파일 조각을 임시 파일로 저장
//            FileOutputStream fos = new FileOutputStream(tempFile, true); // true: append mode
//            fos.write(fileChunk.getBytes());
//            fos.close();
//
//            // 로그 추가
//            System.out.println("File chunk uploaded: " + tempFile.getAbsolutePath());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            // 파일 저장 중 오류 발생 시 예외 처리
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file chunk");
//        }
//        return new ResponseEntity<>("File chunk uploaded successfully", HttpStatus.OK);
//    }
//
//    @PostMapping("/completeUpload")
//    public ResponseEntity<String> completeUpload(@RequestBody String originalFileName) {
//        try {
//            originalFileName = originalFileName.replaceAll("\"", "");
//            // 모든 파일 조각을 결합하여 원본 파일 생성
//            File[] files = new File(UPLOAD_DIR).listFiles();
//            File mergedFile = new File(UPLOAD_DIR, originalFileName);
//            FileOutputStream fos = new FileOutputStream(mergedFile);
//            for (File file : files) {
//                Files.copy(file.toPath(), fos);
//                file.delete(); // 사용한 파일 삭제
//            }
//            fos.close();
//
//            // 로그 추가
//            System.out.println("File merged and uploaded: " + mergedFile.getAbsolutePath());
//
//            return ResponseEntity.ok("File uploaded successfully");
//        } catch (IOException e) {
//            e.printStackTrace();
//            // 파일 결합 중 오류 발생 시 예외 처리
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
//        }
//    }


}
