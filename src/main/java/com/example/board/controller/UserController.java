package com.example.board.controller;

import com.example.board.domain.Course;
import com.example.board.domain.Grade;
import com.example.board.dto.LoginInfo;
import com.example.board.domain.User;
import com.example.board.service.CourseService;
import com.example.board.service.GradeService;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    private final CourseService courseService;

    private final GradeService gradeService;


    // http://localhost:/8080/userRegForm
    // classPath:/templates/userRegForm.html
    @GetMapping("/userRegForm")
    public String userRegForm(){
        return "userRegForm";
    }

    /**
     * 회원 정보를 등록한다.
     * @param name
     * @param email
     * @param password
     * @return
     */
    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){
        System.out.println("name : " + name);
        System.out.println("email : " + email);
        System.out.println("password : " + password);

        // 어떤 기능이 필요한지 미리 알 수 있다. -> interface이다.
        //회원 정보를 저장한다. -> 이 부분을 처리하는게 서비스이다.
        userService.addUser(name, email, password);


        return "redirect:/loginForm"; // 브라우저에게 자동으로 http://localhost:8080/welcome으로 이동
    }

    // http://localhost:8080/welcome
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession httpSession, Model model) {
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo);
        return "mypage";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email, // emmail과 password로 날라오는 파라미터를 String email, String password 변수로 받아준다.
            @RequestParam("password") String password,
            HttpSession httpSession // Spring이 자동으로 Session을 처리하는 HttpSession 객체를 넣어준다. 세션은 현재 브라우저에 접속한 사용자만 접근가능.
            ){

        // email에 해당하는 회원 정보를 읽어온 후
        // 아이디 암호가 맞다면 세션에 회원정보를 저장한다.
        System.out.println(email);
        System.out.println(password);

        try {
            User user = userService.getUser(email);
            System.out.println(user);
            if (user.getPassword().equals(password)){
                System.out.println("암호가 같습니다.");
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());
                // 여기서 포인트* 각각의 브라우저마다 세션이 다르게 만들어진다. -> 서버에 접속한 클라이언트가 10개면 세션이 10개이다.

                // 권한 정보를 읽어와서 loginInfo에 추가한다.
                List<String> roles = userService.getRoles(user.getUserId());
                loginInfo.setRoles(roles);


                httpSession.setAttribute("loginInfo", loginInfo); // 첫번째 파라미터가 key, 두번째 파라미터가 값. (키가 같으면 여러번 로그인 하더라도 나중 값이 덮어 씌워진다.)
                System.out.println("세션에 로그인 정보가 저장된다.");
            }else {
                throw new RuntimeException("암호가 일치하지 않음.");
            }
        }catch (Exception ex){
            return "redirect:/loinForm?error=true"; // 오류가나면 paramater를 붙여서 가도록 함
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession){
        // 세션에서 회원정보를 삭제한다.
        httpSession.removeAttribute("loginInfo");
        return "redirect:/";
    }

    @GetMapping("/participantList")
    public String participantList(Model model,
                                  HttpSession httpSession,
                                  @RequestParam("currentCourseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId);

        Set<User> participants = course.getParticipants();

        model.addAttribute("course", course);
        model.addAttribute("participants", participants);
        model.addAttribute("loginInfo", loginInfo);
        return "participantList";
    }


}
