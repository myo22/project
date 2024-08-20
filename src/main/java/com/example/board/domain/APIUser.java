package com.example.board.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class APIUser {

    @Id
    private String mid;
    private String mpw;

    public void change(String mpw){
        this.mpw = mpw;
    }
}
