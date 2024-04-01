package com.example.board.service;

import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.Repository.VideoRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;


    @Transactional
    public VideoDTO getVideo(int videoId){
        Video video = videoRepository.findById(videoId).orElseThrow();
        return new VideoDTO(video);
    }

    // Video를 VideoDTO로 바꾸는 메소드가 아닌 DTO생성자를 이용해서 매핑해보는 방법을 써봤다.
    @Transactional
    public List<VideoDTO> getAllVideos() {
        List<Video> videos = videoRepository.findAll();
        return videos.stream()
                .map(VideoDTO::new)
                .collect(Collectors.toList());
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
