package com.example.board.Repository.search;

import com.example.board.domain.Board;
import com.example.board.domain.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl(){
        super(Board.class);
    }

    @Override
    public Page<Board> search(Pageable pageable) {

        QBoard board = QBoard.board;

        JPQLQuery<Board> query = from(board);

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(board.title.contains("11")); // title like

        booleanBuilder.or(board.content.contains("11")); // content like

        query.where(booleanBuilder);
        query.where(board.boardId.gt(0L));

//        query.where(board.title.contains("예시")); // where title like ...
//
//        //paging
//        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch(); // JQPLQuery 실행

        long count = query.fetchCount(); // count 쿼리

        return null;
    }
}
