package com.example.board.controller;

import com.example.board.service.AssignmentService;
import com.example.board.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;
}
