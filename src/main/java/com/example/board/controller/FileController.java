package com.example.board.controller;

import com.example.board.dto.LoginInfo;
import com.example.board.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/studyhub")
    public String studyhub(HttpSession httpSession, Model model){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }

        model.addAttribute("loginInfo", loginInfo);
        return "studyhub";
    }


    @ResponseBody
    @PostMapping("/basic")
    public String saveFile(@RequestParam("file") MultipartFile file,
                           @RequestParam("desc") String description) {
        fileService.saveFile(file);
        return "업로드 성공!! - 파일 이름: " + file.getOriginalFilename() + ", 파일 설명: " + description;
    }
}
