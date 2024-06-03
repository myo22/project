package com.example.board.service;

import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.ParticipantRepository;
import com.example.board.Repository.ProgressRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.domain.Course;
import com.example.board.domain.Participant;
import com.example.board.domain.Progress;
import com.example.board.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void AddProgress(int courseId, int userId){
        Course course = courseRepository.getcourse(courseId);
        User user = userRepository.findByUserId(userId);

        Progress progress = Progress.builder()
                .course(course)
                .user(user).build();

        progressRepository.save(progress);
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
