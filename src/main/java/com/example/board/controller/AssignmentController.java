package com.example.board.controller;

import com.example.board.Repository.UserRepository;
import com.example.board.domain.*;
import com.example.board.dto.AssignmentFileDto;
import com.example.board.dto.FileDto;
import com.example.board.dto.LoginInfo;
import com.example.board.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.swing.plaf.SpinnerUI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final CommentService commentService;
    private final ProgressService progressService;

    @GetMapping("/assignmentWriteForm")
    public String assignmentWriteForm(HttpSession httpSession,
                                  Model model,
                                      @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Course course = courseService.getCourse(courseId);
        model.addAttribute("course", course);
        model.addAttribute("loginInfo", loginInfo);

        return "assignmentWriteForm";
    }

    @PostMapping("/assignmentWrite")
    public String assignmentWrite(Model model,
                                  @RequestParam("courseId") int courseId,
                                  @RequestParam("title") String title,
                                @RequestParam("content") String content,
                                  @RequestParam("score") int maxScore,
                                  HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        assignmentService.addAssignment(courseId, loginInfo.getUserId(), maxScore,title, content);
        progressService.incrementAssignmentCount(courseId);
        return "redirect:/assignmentList?currentCourseId=" + courseId;
    }

    @GetMapping("/assignment")
    public  String assignment(@RequestParam("assignmentId") int assignmentId,
                              Model model,
                              HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        Assignment assignment = assignmentService.getAssignment(assignmentId);

        if(assignment.getCourse().getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }else{
            model.addAttribute("isParticipant", true);
        }

        // 이걸 처음 짠 방식인데 이 방식은 간단하지만 데이터가 커질수록 성능에 영향을 줄 수 있다. 즉 유지보수 측면에선 좋지 않을 수 있음.
//        List<AssignmentFile> assignmentFiles = assignment.getAssignmentFiles();
//        AssignmentFile assignmentFile = assignmentFiles.stream()
//                .filter(af -> af.getAssignment().getAssignmentId() == assignmentId)
//                .findFirst()
//                .orElse(null);

        // 이 방법은 대규모 데이터셋에서도 효율적이다. 재사용성도 높아진다.
        AssignmentFileDto assignmentFile = assignmentService.getUserAssignmentFile(loginInfo.getUserId());

        // 이것도 마찬가지
//        List<AssignmentFile> assignmentFiles = assignment.getAssignmentFiles();
//        List<AssignmentFile> participantAssignments = assignmentFiles.stream()
//                .filter(e -> e.getAssignment().getAssignmentId() == assignmentId)
//                .collect(Collectors.toList());
        List<AssignmentFile> assignmentFiles = assignmentService.getAssignmentFiles(assignmentId);
        Set<Comment> comments = commentService.getCommentsByAssignment(assignmentId);

        model.addAttribute("course", assignment.getCourse());
        model.addAttribute("comments", comments);
        model.addAttribute("participantAssignments", assignmentFiles);
        model.addAttribute("assignmentFile", assignmentFile);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("assignment", assignment);
        return "assignment";
    }

    @GetMapping("/assignmentGrade")
    public String assignmentGrade(Model model,
                                  HttpSession httpSession,
                                  @RequestParam("assignmentId") int assignmentId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        List<AssignmentFile> assignmentFiles = assignmentService.getAssignmentFiles(assignmentId);

        model.addAttribute("course", assignment.getCourse());
        model.addAttribute("participantAssignments", assignmentFiles);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("assignment", assignment);

        return "assignmentGrade";
    }

    @PostMapping("/submitScore")
    public String submitScore(@RequestParam("currentCourseId") int currentCourseId,
                              @RequestParam("score") int score,
                              @RequestParam("assignmentId") int assignmentId){

        AssignmentFileDto assignmentFileDto = assignmentService.getAssignmentFile(assignmentId);
        AssignmentFile assignmentFile = assignmentService.toAssignment(assignmentFileDto);

        assignmentFile.setScore(score);
        assignmentService.saveAssignment(assignmentFile);

        return "redirect:/assignmentList?currentCourseId=" + currentCourseId;
    }

    @GetMapping("/assignmentUpdateForm")
    public String assignmentUpdateForm(HttpSession httpSession,
                                       Model model,
                                       @RequestParam("assignmentId") int assignmentId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);

        model.addAttribute("course", assignment.getCourse());
        model.addAttribute("assignment", assignment);
        model.addAttribute("loginInfo", loginInfo);
        return "assignmentUpdateForm";
    }

    @PostMapping("/updateAssignment")
    public String updateAssignment(HttpSession httpSession,
                                       Model model,
                                       @RequestParam("assignmentId") int assignmentId,
                                       @RequestParam("title") String title,
                                       @RequestParam("content") String content,
                                       @RequestParam("score") int maxScore){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        model.addAttribute("assignmentId", assignmentId);
        if(loginInfo == null){
            return "redirect:/loginForm";
        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        if(assignment.getUser().getUserId() != loginInfo.getUserId()){
            return "redirect:/assignment?assignmentId=" + assignmentId;
        }
        assignmentService.updateAssignment(assignmentId, title, content, maxScore);
        return "redirect:/assignment?assignmentId=" + assignmentId;
    }

    @GetMapping("/deleteAssignment")
    public String deleteAssignment(@RequestParam("assignmentId") int assignmentId,
                                  HttpSession httpSession){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginForm";
        }
        // 처음엔 이렇게 했는데 교수면 삭제가 되는게 아니라 해당 강의에 교수여야 삭제 가능해야하기 때문에 바꿔줘야한다.
//        List<String> roles = loginInfo.getRoles();
//        if(roles.contains("ROLE_ADMIN")){
//            assignmentService.deleteAssignment(assignmentId);
//        }
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        if(assignment.getUser().getUserId() == loginInfo.getUserId()){
            assignmentService.deleteAssignment(assignmentId);
        }
        return "redirect:/assignmentList?currentCourseId=" + assignment.getCourse().getCourseId();
    }

    @GetMapping("/assignmentFileWriteForm")
    public String assignmentFileWriteForm(HttpSession httpSession,
                                      Model model,
                                      @RequestParam("assignmentId") int assignmentId,
                                          @RequestParam("courseId") int courseId){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }

        Course course = courseService.getCourse(courseId);
        AssignmentFileDto assignmentFile = assignmentService.getUserAssignmentFile(loginInfo.getUserId());
        Assignment assignment = assignmentService.getAssignment(assignmentId);

        if(assignment.getCourse().getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }else{
            model.addAttribute("isParticipant", true);
        }

        model.addAttribute("assignmentFile", assignmentFile);
        model.addAttribute("loginInfo", loginInfo);
        model.addAttribute("assignment", assignment);
        model.addAttribute("course", course);

        return "assignmentFileWriteForm";
    }

    @PostMapping("/assignmentFileWrite")
    public String assignmentFileWrite(@RequestParam("file")MultipartFile file,
                                  @RequestParam("assignmentId") int assignmentId,
                                  @RequestParam("userId") int userId,
                                      @RequestParam("courseId") int courseId) {
        try {
            assignmentService.saveFile(file, assignmentId, userId);
        }catch (IOException e){
            e.printStackTrace();
        }
        progressService.submitAssignments(courseId, userId);
        return "redirect:/assignment?assignmentId=" + assignmentId;
    }

    @GetMapping("/assignmentList")
    public String assignmentList(HttpSession httpSession,
                                 Model model,
                                 @RequestParam("currentCourseId") Integer currentCourseId,
                                 @RequestParam(name = "page", defaultValue = "0") int page){
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginForm";
        }

        long total = assignmentService.getTotalCount();
        List<Assignment> list = assignmentService.getAssignments(page);
        long pageCount = total / 10;
        if(total % 10 > 0){
            pageCount++;
        }
        int currentPage = page;

        Course course = courseService.getCourse(currentCourseId);
        if(course.getUser().getUserId() == loginInfo.getUserId()){
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("loginInfo",loginInfo);
        model.addAttribute("course", course);
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount); // 총 페이지 수를 나타내는것 같다.
        model.addAttribute("currentPage",currentPage); // 현재 페이지 번호

        return "assignmentList";

    }

    @GetMapping("/Assignment/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") int fileId) throws IOException {
        AssignmentFileDto assignmentFileDto = assignmentService.getFile(fileId);
        Path path = Paths.get(assignmentFileDto.getAssignmentPath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        // 파일 다운로드에 사용될 파일 이름 설정
        String filename = assignmentFileDto.getOrigFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                // Content-Disposition 헤더 설정하여 파일 이름 지정
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }



}
