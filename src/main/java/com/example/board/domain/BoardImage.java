package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = "board")
public class BoardImage implements Comparable<BoardImage>{ // Comparable 적용하는건 @OneToMany 처리에서 순서에 맞게 정렬하기 위함이다.

    @Id
    private String uuid;

    private String fileName;

    private int ord;

    @ManyToOne
    public Board board;

    @Override
    public int compareTo(BoardImage other) {
        return this.ord - other.ord;
    }

    public void changeBoard(Board board){
        this.board = board;
    }
}
