package com.example.board.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/sample")
public class SampleController {

    @ApiOperation("Sample GET doA")
    @GetMapping("/doA")
    public List<String> doA(){
        return Arrays.asList("AAA", "BBB", "CCC");
    }
}
