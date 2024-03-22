package com.example.board.service;

import com.example.board.Repository.AssignmentRepository;
import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.domain.Assignment;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import com.example.board.dto.AssignmentDto;
import com.example.board.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public void saveFile(MultipartFile file, int courseId, int userId) throws IOException {
        Course course = courseRepository.getcourse(courseId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Course not found"));


        // 파일 정보 저장
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        String filePath = uploadDir + File.separator + filename; // 파일 경로 변경
        file.transferTo(new File(filePath));

        // 파일 정보를 DB에 저장
        Assignment assignment = Assignment.builder()
                .origFilename(originalFilename)
                .assignmentName(filename)
                .assignmentPath(filePath)
                .course(course)
                .user(user)
                .build();

        assignmentRepository.save(assignment);
    }

    // 모든 파일 목록 조회
    public List<AssignmentDto> getAllFiles() {
        List<Assignment> assignments = assignmentRepository.findAll();
        return assignments.stream()
                .map(this::toAssignmentDto)
                .collect(Collectors.toList());
    }

    // 파일 ID로 파일 정보 조회
    public AssignmentDto getFile(int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + assignmentId));
        return toAssignmentDto(assignment);
    }

    // AttachedFile을 FileDto로 변환하는 메서드
    private AssignmentDto toAssignmentDto(Assignment assignment) {
        return AssignmentDto.builder()
                .assignmentId(assignment.getAssignmentId())
                .origFilename(assignment.getOrigFilename())
                .assignmentName(assignment.getAssignmentName())
                .assignmentPath(assignment.getAssignmentPath())
                .courseId(assignment.getCourse().getCourseId())
                .userId(assignment.getUser().getUserId())
                .build();
    }
}
