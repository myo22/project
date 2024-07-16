package com.example.board.controller;

import com.example.board.domain.Board;
import com.example.board.domain.Course;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.LoginInfo;
import com.example.board.dto.PageRequestDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.service.BoardServiceImpl;
import com.example.board.service.CourseService;
import com.example.board.service.ProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

// HTTP요청을 받아서 응답을 받는 컴포넌트, 스프링 부트가 자동으로 Bean으로 생성한다.
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
@Log4j2
public class BoardController {

    private final BoardServiceImpl boardService;
    private final CourseService courseService;
    private final ProgressService progressService;

    // 게시물 목록을 보여준다.
    // http://localhost:8080/ -----> "list"라는 이름의 템플릿을 사용(forward)하여 화면에 출력.
    // list를 리턴한다는 것은 classpath:/templates/list.html을 사용한다는 뜻이다. classpath:/경로나  .html(확장자)를 바꿔주고 싶다면 prefix랑 suffix를 바꿔주면 가능하다.
    @GetMapping("/list")
    public void list(HttpSession httpSession,
                       Model model,
                       PageRequestDTO pageRequestDTO,
                       @RequestParam("currentCourseId") int courseId){ // HttpSession, Model은 Spring이 자동으로 넣어준다.
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

//        // 게시물 목록을 읽어온다. 페이징 처리한다.
//        long total = boardService.getTotalCount(); // 11
//        List<Board> list = boardService.getBoards(page); // page가 1,2,3,4 ....
//        long pageCount = total / 10; // 1;
//        if (total % 10> 0) { // 나머지가 있을 경우 1page를 추가
//            pageCount++;
//        }
//        int currentPage = page;


        Course course = courseService.getCourse(courseId);

        if(course.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }

        model.addAttribute("loginInfo", loginInfo); // 모델은 템플릿에 값을 넘겨주기위한 객체
        model.addAttribute("course", course);
        model.addAttribute("responseDTO", responseDTO);

    }

    // /board?id=3 // 물음표 뒤에 값은 파라미터 id, 파라미터 id의 값은 3
    // /board?id=3
    // /board?id=3
    @GetMapping(value = {"/read", "/modify"})
    public void read(PageRequestDTO pageRequestDTO,
                        Long bno,
                        Model model,
                        HttpSession httpSession,
                        @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");

        // id에 해당하는 게시물을 읽어온다.
        // id에 해당하는 게시물의 조회수도 1증가한다.
        BoardDTO boardDTO = boardService.readOne(bno);
        progressService.watchDiscussions(courseId, loginInfo.getUserId());

        Course course = courseService.getCourse(courseId);

        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo); // 모델은 템플릿에 값을 넘겨주기위한 객체
        model.addAttribute("dto", boardDTO);

    }

    // 삭제한다. 관리자는 모든 글을 삭제할 수 있다.
    // 수정한다.

    @GetMapping("/register")
    public void registerGET(HttpSession httpSession, Model model,
                            @RequestParam("courseId") int courseId){
        // 로그인한 사용자만 글을 써야한다. 로그인을 하지 않았다면 리스트 보기로 자동 이동 시킨다.
        // 세션에서 로그인한 정보를 읽어들인다.
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");

        Course course = courseService.getCourse(courseId);

        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo);

    }

    @PostMapping("/register")
    public String registerPOST(
            @Valid BoardDTO boardDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpSession httpSession
    ){
        log.info("board POST register...");

        // 로그인한 사용자만 글을 써야한다. 로그인을 하지 않았다면 리스트 보기로 자동 이동 시킨다.
        // 세션에서 로그인한 정보를 읽어들인다.
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }

        if (bindingResult.hasErrors()) {
            log.info("has errors.....");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            return "redirect:/board/register?currentCourseId=" + boardDTO.getCourseId();
        }

        log.info(boardDTO);

        Long bno = boardService.register(boardDTO);

        redirectAttributes.addFlashAttribute("result", bno);

        progressService.incrementDiscussionCount(boardDTO.getCourseId());

        // 컨트롤러의 메소드가 리턴하는 문자열은 템플릿 이름이다.
        return "redirect:/board/list?currentCourseId=" + boardDTO.getCourseId(); // 리스트 보기로 리다이렉트한다.

    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") Long boardId,
            HttpSession httpSession
    ){
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }

        // loginInfo.getUserId() 사용자가 쓴 글일 경우에만 삭제한다.
        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else{
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }

        return "redirect:/"; // 리스트 보기로 리다이렉트한다.
    }

    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId") Long boardId,
                             Model model,
                             HttpSession httpSession,
                             @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }

        //boardId에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달한다.
        Board board =boardService.getBoard(boardId, false);
        Course course = courseService.getCourse(courseId);

        model.addAttribute("course", course);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }

    @PostMapping("/update")
    public String update(@RequestParam("boardId") Long boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }

        Board board =boardService.getBoard(boardId, false);
        if(board.getUser().getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId=" + boardId; // 글보기로 이동한다.
        }

        // boardId에 해당하는 글의 제목과 내용을 수정한다.
        boardService.updateBoard(boardId, title, content);
        // 글쓴이만 수정 가능.
        return "redirect:/board?boardId=" + boardId; // 수정된 글 보기로 리다이렉트한다.
    }
}
