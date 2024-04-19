package com.example.board.service;

import com.example.board.Repository.AssignmentFileRepository;
import com.example.board.Repository.AssignmentRepository;
import com.example.board.Repository.CourseRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.domain.Assignment;
import com.example.board.domain.AssignmentFile;
import com.example.board.domain.Course;
import com.example.board.domain.User;
import com.example.board.dto.AssignmentFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentFileRepository assignmentFileRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Transactional
    public void addAssignment(int courseId, int userId, int maxScore, String title, String content){
        Course course = courseRepository.getcourse(courseId);
        User user = userRepository.findById(userId).orElseThrow();
        Assignment assignment = Assignment.builder()
                .content(content)
                .maxScore(maxScore)
                .title(title)
                .course(course)
                .user(user)
                .build();

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void save(Assignment assignment){
        assignmentRepository.save(assignment);
    }

    @Transactional
    public void updateAssignment(int assignmentId, String title, String content, int maxScore){
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
        assignment.setTitle(title);
        assignment.setContent(content);
        assignment.setMaxScore(maxScore);
    }

    @Transactional
    public void deleteAssignment(int assignmentId){
        assignmentRepository.deleteById(assignmentId);
    }

    @Transactional
    public Assignment getAssignment(int assignmentId){
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
        return assignment;
    }

    @Transactional
    public AssignmentFileDto getAssignmentFile(int assignmentId){
        AssignmentFile assignmentFile = assignmentFileRepository.getAssignmentFile(assignmentId);
        return toAssignmentDto(assignmentFile);
    }

    @Transactional(readOnly = true)
    public Long getTotalCount() {
        return assignmentRepository.getAssignmentCount();
    }

    @Transactional(readOnly = true)
    public List<Assignment> getAssignments(int page){
        Pageable pageable = PageRequest.of(page, 10);
        return assignmentRepository.findByOrderByRegdateDesc(pageable).getContent();
    }

    @Transactional
    public void saveFile(MultipartFile file, int assignmentId, int userId) throws IOException {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new IllegalArgumentException("user not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Course not found"));


        // 파일 정보 저장
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        String filePath = uploadDir + File.separator + filename; // 파일 경로 변경
        file.transferTo(new File(filePath));

        // 파일 정보를 DB에 저장
        AssignmentFile assignmentFile = AssignmentFile.builder()
                .origFilename(originalFilename)
                .assignmentName(filename)
                .assignmentPath(filePath)
                .user(user)
                .assignment(assignment)
                .build();

        assignmentFileRepository.save(assignmentFile);
    }

    // 모든 파일 목록 조회
    @Transactional
    public List<AssignmentFileDto> getAllFiles() {
        List<AssignmentFile> assignmentFiles = assignmentFileRepository.findAll();
        return assignmentFiles.stream()
                .map(this::toAssignmentDto)
                .collect(Collectors.toList());
    }

    // 파일 ID로 파일 정보 조회
    @Transactional
    public AssignmentFileDto getFile(int assignmentFileId) {
        AssignmentFile assignmentFile = assignmentFileRepository.findById(assignmentFileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + assignmentFileId));
        return toAssignmentDto(assignmentFile);
    }

    // AttachedFile을 FileDto로 변환하는 메서드
    private AssignmentFileDto toAssignmentDto(AssignmentFile assignmentFile) {
        return AssignmentFileDto.builder()
                .assignmentFileId(assignmentFile.getAssignmentFileId())
                .origFilename(assignmentFile.getOrigFilename())
                .assignmentName(assignmentFile.getAssignmentName())
                .assignmentPath(assignmentFile.getAssignmentPath())
                .userId(assignmentFile.getUser().getUserId())
                .assignmentId(assignmentFile.getAssignment().getAssignmentId())
                .build();
    }
}
