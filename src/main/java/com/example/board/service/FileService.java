package com.example.board.service;

import com.example.board.Repository.FileRepository;
import com.example.board.domain.AttachedFile;
import com.example.board.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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


    // 파일을 업로드하고 DB에 정보 저장
    public FileDto uploadFile(MultipartFile file) throws IOException {
        // 파일 정보 저장
        AttachedFile attachedFile = AttachedFile.builder()
                .origFilename(file.getOriginalFilename())
                .filename(file.getOriginalFilename())
                .filePath("uploads/")  // 저장된 경로를 변경할 수 있습니다.
                .build();

        // 파일 정보를 DB에 저장
        attachedFile = fileRepository.save(attachedFile);

        return toFileDto(attachedFile);
    }

    // 파일 정보 저장
    public void saveFile(FileDto fileDto) {
        AttachedFile attachedFile = AttachedFile.builder()
                .origFilename(fileDto.getOrigFilename())
                .filename(fileDto.getFilename())
                .filePath(fileDto.getFilePath())
                .build();
        fileRepository.save(attachedFile);
    }

    // 모든 파일 목록 조회
    public List<FileDto> getAllFiles() {
        List<AttachedFile> attachedFiles = fileRepository.findAll();
        return attachedFiles.stream()
                .map(this::toFileDto)
                .collect(Collectors.toList());
    }

    // 파일 ID로 파일 정보 조회
    public FileDto getFile(Long fileId) {
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
                .build();
    }
}