package com.example.board.service;

import com.example.board.Repository.*;
import com.example.board.domain.Course;
import com.example.board.domain.Participant;
import com.example.board.domain.Progress;
import com.example.board.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final VideoRepository videoRepository;
    private final NoticeRepository noticeRepository;
    private final AssignmentRepository assignmentRepository;
    private final BoardRepository boardRepository;
    private final FileRepository fileRepository;

    @Transactional
    public void AddProgress(int courseId, int userId){
        Course course = courseRepository.getcourse(courseId);
        User user = userRepository.findByUserId(userId);

        int countVideos = videoRepository.countByCourseCourseId(courseId);
        int countAssignments = assignmentRepository.countByCourseCourseId(courseId);
        int countDiscussions = boardRepository.countByCourseCourseId(courseId);
        int countNotices = noticeRepository.countByCourseCourseId(courseId);
        int countResources = fileRepository.countByCourseCourseId(courseId);

        Progress progress = Progress.builder()
                .totalVideos(countVideos)
                .totalAssignments(countAssignments)
                .totalDiscussions(countDiscussions)
                .totalNotices(countNotices)
                .totalResources(countResources)
                .course(course)
                .user(user).build();

        progressRepository.save(progress);
    }

    @Transactional
    public Set<Progress> getProgresses(int userId){
        return progressRepository.findByUserUserId(userId);
    }

    @Transactional
    public void incrementVideoCount(int courseId){
        Set<Progress> progresses = progressRepository.findByCourseCourseId(courseId);
        if(progresses.isEmpty()){
            return;
        }
        for(Progress progress : progresses){
            progress.setTotalVideos(progress.getTotalVideos() + 1);
        }
    }

    @Transactional
    public void incrementAssignmentCount(int courseId){
        Set<Progress> progresses = progressRepository.findByCourseCourseId(courseId);
        if(progresses.isEmpty()){
            return;
        }
        for(Progress progress : progresses){
            progress.setTotalAssignments(progress.getTotalAssignments() + 1);
        }
    }

    @Transactional
    public void incrementDiscussionCount(int courseId){
        Set<Progress> progresses = progressRepository.findByCourseCourseId(courseId);
        if(progresses.isEmpty()){
            return;
        }
        for(Progress progress : progresses){
            progress.setTotalDiscussions(progress.getTotalDiscussions() + 1);
        }
    }

    @Transactional
    public void incrementNoticeCount(int courseId){
        Set<Progress> progresses = progressRepository.findByCourseCourseId(courseId);
        if(progresses.isEmpty()){
            return;
        }
        for(Progress progress : progresses){
            progress.setTotalNotices(progress.getTotalNotices() + 1);
        }
    }

    @Transactional
    public void incrementResourceCount(int courseId){
        Set<Progress> progresses = progressRepository.findByCourseCourseId(courseId);
        if(progresses.isEmpty()){
            return;
        }
        for(Progress progress : progresses){
            progress.setTotalResources(progress.getTotalResources() + 1);
        }
    }

    @Transactional
    public void watchVideos(int courseId, int userId){
        Progress progress = progressRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        Participant participant = participantRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        if(participant.isVideoWatched() == false){
            progress.setTotalVideos(progress.getTotalVideos() - 1);
            participant.setVideoWatched(true);
            participantRepository.save(participant);
        }
        progressRepository.save(progress);
    }

    @Transactional
    public void submitAssignments(int courseId, int userId){
        Progress progress = progressRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        Participant participant = participantRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        if(participant.isAssignmentSubmit() == false){
            progress.setTotalAssignments(progress.getTotalAssignments() - 1);
            participant.setAssignmentSubmit(true);
            participantRepository.save(participant);
        }
        progressRepository.save(progress);
    }

    @Transactional
    public void watchDiscussions(int courseId, int userId){
        Progress progress = progressRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        Participant participant = participantRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        if(participant.isDiscussionWatched() == false){
            progress.setTotalDiscussions(progress.getTotalDiscussions() - 1);
            participant.setDiscussionWatched(true);
            participantRepository.save(participant);
        }
        progressRepository.save(progress);
    }

    @Transactional
    public void watchNotices(int courseId, int userId){
        Progress progress = progressRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        Participant participant = participantRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        if(participant.isNoticeWatched() == false){
            progress.setTotalNotices(progress.getTotalNotices() - 1);
            participant.setNoticeWatched(true);
            participantRepository.save(participant);
        }
        progressRepository.save(progress);
    }

    @Transactional
    public void watchResources(int courseId, int userId){
        Progress progress = progressRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        Participant participant = participantRepository.findByCourseCourseIdAndUserUserId(courseId, userId);
        if(participant.isResourceWatched() == false){
            progress.setTotalResources(progress.getTotalResources() - 1);
            participant.setResourceWatched(true);
            participantRepository.save(participant);
        }
        progressRepository.save(progress);
    }
}
