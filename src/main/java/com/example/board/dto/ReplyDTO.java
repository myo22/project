package com.example.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDTO {


    private Long rno;

    @NotNull
    private Long bno;

    @NotEmpty
    private String replyText;

    @NotEmpty
    private String replyer;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    @JsonIgnore // Json으로 변환될 때 제외하도록 적용 (출력할 일 X)
    private LocalDateTime modDate;

}
