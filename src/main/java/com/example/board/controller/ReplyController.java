package com.example.board.controller;

import com.example.board.dto.ReplyDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

// RestController를 이용하면 메소드의 모든 리턴 값은 JSP나 Thymeleaf로 전송되는게 아니라 바로 JSON이나 XML 등으로 처리됩니다.
@RestController
@RequestMapping("/replies")
@Log4j2
public class ReplyController {

    // APiOperation은 Swagger UI에서 해당 기능의 설명으로 출력된다.
    @ApiOperation(value = "Replies Post", notes = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE) // 메소드를 받아서 소비하는 데이터가 어떤 종류인지 명시
    public Map<String, Long> register(@Valid @RequestBody ReplyDTO replyDTO,
                                                      BindingResult bindingResult)throws BindException{ // @RequestBody는 JSON문자열을 replyDTO로 바꾸기 위해 사용됩니다.

        log.info(replyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", 111L);

        return resultMap;
    }
}
