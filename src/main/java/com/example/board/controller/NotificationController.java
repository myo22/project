package com.example.board.controller;


import com.example.board.dto.LoginInfo;
import com.example.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;


@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    @GetMapping("/notify")
    public SseEmitter handle(HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo != null) {
            return notificationService.connectNotification(loginInfo.getUserId());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in.");
        }
    }
}
