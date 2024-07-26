package com.example.board.controller;

import com.example.board.dto.upload.UploadFileDTO;
import com.example.board.dto.upload.UploadResultDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpDownController {

    // @Value는 application.yml 파일의 설정 정보를 읽어서 변수의 값으로 사용할 수 있습니다. uploadPath는 나중에 파일을 업로드하는 경로로 사용합니다.
    @Value("${org.zerock.upload.path}") // import 시에 springframework으로 시작하는 Value
    private String uploadPath;

    @ApiOperation(value = "Upload POST", notes = "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO){

        log.info(uploadFileDTO);

        if(uploadFileDTO.getFiles() != null){

            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {

                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath); // 실제 파일 저장

                    // 이미지 파일 종류라면
                    if(Files.probeContentType(savePath).startsWith("image")){

                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);

                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

                list.add(UploadResultDTO.builder()
                                .uuid(uuid)
                        .fileName(originalName)
                        .img(image).build()
                );

            }); //end each

            return list;
        }// end if

        return null;
    }
}
