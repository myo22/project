package com.example.board.service;

import com.example.board.Repository.CourseRepository;
import java.io.FileNotFoundException;
import com.example.board.Repository.FileRepository;
import com.example.board.domain.AttachedFile;
import com.example.board.domain.Course;
import com.example.board.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final CourseRepository courseRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

//    // 파일을 업로드하고 DB에 정보 저장
//    public FileDto uploadFile(MultipartFile file, int courseId) throws IOException {
//        Course course = courseService.getCourse(courseId);
//        // 파일 정보 저장
//        AttachedFile attachedFile = AttachedFile.builder()
//                .origFilename(file.getOriginalFilename())
//                .filename(file.getOriginalFilename())
//                .filePath("uploads/")  // 저장된 경로를 변경할 수 있습니다.
//                .course(course)
//                .build();
//
//        // 파일 정보를 DB에 저장
//        attachedFile = fileRepository.save(attachedFile);
//
//        return toFileDto(attachedFile);
//    }

    // 파일 정보 저장
    @Transactional
    public void saveFile(MultipartFile file, int courseId) throws IOException {
        Course course = courseRepository.getcourse(courseId);

        // 파일 정보 저장
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        String filePath = uploadDir + File.separator + filename; // 파일 경로 변경
        file.transferTo(new File(filePath));

        // 파일 정보를 DB에 저장
        AttachedFile attachedFile = AttachedFile.builder()
                .origFilename(originalFilename)
                .filename(filename)
                .filePath(filePath)
                .course(course)
                .build();

        fileRepository.save(attachedFile);
    }

    @Transactional
    public void deleteFile(int fileId){
        AttachedFile attachedFile = fileRepository.findById(fileId).orElseThrow();

        fileRepository.delete(attachedFile);

        Path filePath = Paths.get(attachedFile.getFilePath());
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + attachedFile.getOrigFilename(), e);
        }
    }

    // 모든 파일 목록 조회
    @Transactional
    public List<FileDto> getAllFiles() {
        List<AttachedFile> attachedFiles = fileRepository.findAll();
        return attachedFiles.stream()
                .map(this::toFileDto)
                .collect(Collectors.toList());
    }

    // 파일 ID로 파일 정보 조회
    @Transactional
    public FileDto getFile(int fileId) {
        AttachedFile attachedFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + fileId));
        return toFileDto(attachedFile);
    }

    // AttachedFile을 FileDto로 변환하는 메서드
    private FileDto toFileDto(AttachedFile attachedFile) {
        return FileDto.builder()
                .fileId(attachedFile.getFileId())
                .origFilename(attachedFile.getOrigFilename())
                .filename(attachedFile.getFilename())
                .filePath(attachedFile.getFilePath())
                .courseId(attachedFile.getCourse().getCourseId())
                .build();
    }
}