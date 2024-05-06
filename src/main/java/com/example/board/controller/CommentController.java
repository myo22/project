package com.example.board.controller;

import com.example.board.domain.User;
import com.example.board.dto.LoginInfo;
import com.example.board.service.CommentService;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;


//    @PostMapping("/submitCommentToAssignment")
//    public String submitCommentToAssignment(@RequestParam("comment") String comment,
//                                            @RequestParam("assignmentId") int assignmentId,
//                                            HttpSession httpSession){
//        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
//        User user = userService.getUser(loginInfo.getUserId());
//
//        commentService.addCommentToAssignment(assignmentId, comment, user);
//
//        return "redirect:/assignment?assignmentId=" + assignmentId;
//    }
//
//    @PostMapping("/submitCommentToVideo")
//    public String submitCommentToVideo(@RequestParam("comment") String comment,
//                                       @RequestParam("videoId") int videoId,
//                                       HttpSession httpSession){
//        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
//        User user = userService.getUser(loginInfo.getUserId());
//
//        commentService.addCommentToVideo(videoId, comment, user);
//
//        return "redirect:/video?videoId=" + videoId;
//    }

    // 위처럼 두개로 나눠서했다가 같은 동작을 할때는 아래와같이 합쳐서 유연하게 만들 수 있다고 알아냈다.
    @PostMapping("/submitComment")
    public String submitComment(@RequestParam("comment") String comment,
                                @RequestParam("contentId") int contentId,
                                @RequestParam("contentType") String contentType,
                                HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        User user = userService.getUser(loginInfo.getUserId());
        switch (contentType){
            case "assignment":
                commentService.addComment(contentId, comment, user, contentType);
                return "redirect:/assignment?assignmentId=" + contentId;
            case "video":
                commentService.addComment(contentId, comment, user, contentType);
                return "redirect:/video?videoId=" + contentId;
            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }
}
