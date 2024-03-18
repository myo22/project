package com.example.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

    public void saveFile(MultipartFile file){
        if(!file.isEmpty()){
            // 파일을 저장할 path 선언
            // project root path 밑에 video 디렉토리에 저장
            Path filepath = Paths.get("video", file.getOriginalFilename());

            // 해당 path의 디렉토리가 존재하지 않으면 생성
            if (!Files.exists(filepath.getParent())) {
                try {
                    Files.createDirectories(filepath.getParent());
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장 디렉토리 생성 중 오류 발생", e);
                }
            }

            // 해당 path 에 파일의 스트림 데이터를 저장
            try (OutputStream os = Files.newOutputStream(filepath)){
                os.write(file.getBytes());
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
