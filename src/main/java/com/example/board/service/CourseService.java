package com.example.board.service;

import com.example.board.Repository.BoardRepository;
import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.domain.Board;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void addCourse(int userId, String title, String content) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = new Course();
        course.setUser(user);
        course.setTitle(title);
        course.setContent(content);
        courseRepository.save(course);
    }

    @Transactional
    public void joinCourse(int courseId, int userId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        course.getParticipants().add(user);
        user.getCourses().add(course);

        courseRepository.save(course);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Course> getUserCourses(User user) {
        return courseRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Long getTotalCount() {
        return courseRepository.getCourseCount();
    }

    @Transactional(readOnly = true)
    public List<Course> getCourses(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return courseRepository.findByOrderByRegdateDesc(pageable).getContent();
    }


    @Transactional
    public Course getCourse(int courseId) {
        return courseRepository.findById(courseId).orElseThrow();
    }

    @Transactional
    public void deleteCourse(int userId, int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        if (course.getUser().getUserId() == userId) {
            courseRepository.delete(course);
        }
    }

    @Transactional
    public void deleteCourse(int courseId) {
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public void updateCourse(int courseId, String title, String content) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setTitle(title);
        course.setContent(content);
    }

}
