package com.example.board.service;

import com.example.board.Repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
}
