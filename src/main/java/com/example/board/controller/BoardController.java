package com.example.board.controller;

import com.example.board.domain.Board;
import com.example.board.domain.Course;
import com.example.board.dto.*;
import com.example.board.service.BoardServiceImpl;
import com.example.board.service.CourseService;
import com.example.board.service.ProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import java.io.File;
import java.nio.file.Files;
import java.util.List;

// HTTP요청을 받아서 응답을 받는 컴포넌트, 스프링 부트가 자동으로 Bean으로 생성한다.
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
@Log4j2
public class BoardController {

    @Value("${org.zerock.upload.path}") // import 시에 springframework으로 시작하는 Value
    private String uploadPath;

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
                       @RequestParam("courseId") int courseId){ // HttpSession, Model은 Spring이 자동으로 넣어준다.
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");

//        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);

        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        log.info(responseDTO);

//        // 게시물 목록을 읽어온다. 페이징 처리한다.
//        long total = boardService.getTotalCount(); // 11
//        List<Board> list = boardService.getBoards(page); // page가 1,2,3,4 ....
//        long pageCount = total / 10; // 1;
//        if (total % 10> 0) { // 나머지가 있을 경우 1page를 추가
//            pageCount++;
//        }
//        int currentPage = page;


        Course course = courseService.getCourse(courseId);

        model.addAttribute("loginInfo", loginInfo); // 모델은 템플릿에 값을 넘겨주기위한 객체
        model.addAttribute("course", course);
        model.addAttribute("responseDTO", responseDTO);

    }

    // /board?id=3 // 물음표 뒤에 값은 파라미터 id, 파라미터 id의 값은 3
    // /board?id=3
    // /board?id=3
    @GetMapping({"/read", "/modify"})
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

        if(course.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }

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

        log.info("register 등록 시작");

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

            return "redirect:/board/register?courseId=" + boardDTO.getCourseId();
        }

        log.info(boardDTO);

        Long bno = boardService.register(boardDTO);

        redirectAttributes.addFlashAttribute("result", bno);
        redirectAttributes.addAttribute("courseId", boardDTO.getCourseId());

        progressService.incrementDiscussionCount(boardDTO.getCourseId());

        // 컨트롤러의 메소드가 리턴하는 문자열은 템플릿 이름이다.
        return "redirect:/board/list"; // 리스트 보기로 리다이렉트한다.

    }

    @PostMapping("/modify")
    public String modify(@Valid BoardDTO boardDTO,
                         PageRequestDTO pageRequestDTO,
                         HttpSession httpSession,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes){
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }

        log.info("board modify post......." + boardDTO);

        if(bindingResult.hasErrors()){
            log.info("has errors.....");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            redirectAttributes.addAttribute("courseId", boardDTO.getCourseId());

            // link 변수가 이미 완성된 URL 쿼리 문자열이기 때문에 addAttribute() 메서드를 사용하지 않습니다.
            return "redirect:/board/modify?" + link;
        }

        boardService.modify(boardDTO);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        redirectAttributes.addAttribute("courseId", boardDTO.getCourseId());

        return "redirect:/board/read";
    }


    @PostMapping("/remove")
    public String remove(
            @Valid BoardDTO boardDTO,
            HttpSession httpSession,
            RedirectAttributes redirectAttributes
    ){
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }

        Long bno = boardDTO.getBno();
        log.info("remove post.. " + bno);

        boardService.remove(bno);

        // 게시물이 삭제되었다면 첨부 파일 삭제
        log.info(boardDTO.getFileNames());
        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames.size() > 0 && fileNames != null){
            removeFiles(fileNames);
        }

        redirectAttributes.addAttribute("result", "removed");
        redirectAttributes.addAttribute("courseId", boardDTO.getCourseId());

        return "redirect:/board/list"; // 리스트 보기로 리다이렉트한다.
    }

    public void removeFiles(List<String> files){

        for(String fileName : files){

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();

            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                // 섬네일이 존재한다면
                if (contentType.startsWith("image")) {
                    File tumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    tumbnailFile.delete();
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }
}
