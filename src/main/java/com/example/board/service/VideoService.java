package com.example.board.service;

import com.example.board.Repository.AttendanceRepository;
import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.Repository.VideoRepository;
import com.example.board.domain.Attendance;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import com.example.board.domain.Video;
import com.example.board.dto.VideoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public VideoDTO getVideo(int videoId){
        Video video = videoRepository.findById(videoId).orElseThrow();
        return new VideoDTO(video);
    }

    // Video를 VideoDTO로 바꾸는 메소드가 아닌 DTO생성자를 이용해서 매핑해보는 방법을 써봤다.
    @Transactional
    public List<VideoDTO> getAllVideos(int userId) {
        List<Video> videos = videoRepository.findAll();
        List<VideoDTO> videoDTOS = new ArrayList<>();

        for(Video video : videos){
            VideoDTO videoDTO = new VideoDTO();
            videoDTO.setVideoId(video.getVideoId());
            videoDTO.setVideoName(videoDTO.getVideoName());
            videoDTO.setVideoUrl(video.getVideoUrl());
            videoDTO.setTitle(video.getTitle());
            videoDTO.setCourseId(video.getCourse().getCourseId());
            videoDTO.setUserId(video.getUser().getUserId());
            videoDTO.setUserName(video.getUser().getName());

            Attendance attendance = attendanceRepository.findByVideoVideoIdAndUserUserId(video.getVideoId(), userId);

            if(attendance != null){
                videoDTO.setAttendanceStatus(attendance.getAttendanceStatus());
            } else {
                videoDTO.setAttendanceStatus("결석");
            }

            videoDTOS.add(videoDTO);
        }
        return videoDTOS;

    }

    @Transactional
    public Video uploadVideo(MultipartFile file, String title, int courseId, int userId) throws IOException {
        Course course = courseRepository.getcourse(courseId);
        User user = userRepository.findById(userId).orElseThrow();

        String videoName = StringUtils.cleanPath(file.getOriginalFilename());
        String videoUrl = "/uploads/" + videoName;

        Video video = Video.builder()
                .videoName(videoName)
                .videoUrl(videoUrl)
                .title(title)
                .course(course)
                .user(user).build();

        videoRepository.save(video);
        return video;
    }

    @Transactional
    public Video updateVideo(int videoId, String title, MultipartFile file) {
        Video video = videoRepository.findById(videoId).orElseThrow();
        String videoName = StringUtils.cleanPath(file.getOriginalFilename());
        String videoUrl = "/uploads/" + videoName;

        video.setVideoName(videoName);
        video.setVideoUrl(videoUrl);
        video.setTitle(title);

        return video;
    }

    @Transactional
    public void deleteVideo(int videoId){
        videoRepository.deleteById(videoId);
    }

    public Video toVideo(VideoDTO videoDTO){
        User user = userRepository.findById(videoDTO.getUserId()).orElseThrow();
        Course course = courseRepository.findById(videoDTO.getCourseId()).orElseThrow();
        return Video.builder()
                .videoId(videoDTO.getVideoId())
                .videoName(videoDTO.getVideoName())
                .videoUrl(videoDTO.getVideoUrl())
                .title(videoDTO.getTitle())
                .course(course)
                .user(user)
                .build();
    }

//    private VideoDTO toVideoDTO(Video video){
//        return VideoDTO.builder()
//                .videoId(video.getVideoId())
//                .videoName(video.getVideoName())
//                .videoUrl(video.getVideoUrl())
//                .title(video.getTitle())
//                .courseId(video.getCourse().getCourseId())
//                .userId(video.getUser().getUserId())
//                .build();
//    }
}
