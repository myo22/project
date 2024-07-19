package com.example.board.service;

import com.example.board.Repository.ReplyRepository;
import com.example.board.domain.Board;
import com.example.board.domain.Reply;
import com.example.board.dto.PageRequestDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.ReplyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService{

    private final ModelMapper modelMapper;
    private final ReplyRepository replyRepository;

    @Override
    public Long register(ReplyDTO replyDTO) {
        Reply reply = modelMapper.map(replyDTO, Reply.class);

        Long rno = replyRepository.save(reply).getRno();

        return rno;
    }

    @Override
    public ReplyDTO read(Long rno) {
        Optional<Reply> result = replyRepository.findById(rno);
        Reply reply = result.orElseThrow();

        return  modelMapper.map(reply, ReplyDTO.class);
    }

    @Override
    public void Modify(ReplyDTO replyDTO) {
        Optional<Reply> result = replyRepository.findById(replyDTO.getRno());

        Reply reply = result.orElseThrow();

        reply.changeText(replyDTO.getReplyText());

        replyRepository.save(reply).getRno();
    }

    @Override
    public void remove(Long rno) {

        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {


        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0? 0 : pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("rno").ascending());

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        List<ReplyDTO> dtoList = result.getContent().stream().map((reply) -> modelMapper.map(reply, ReplyDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }
}
