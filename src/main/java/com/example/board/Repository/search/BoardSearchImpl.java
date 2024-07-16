package com.example.board.Repository.search;

import com.example.board.domain.Board;
import com.example.board.domain.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        query.where(board.bno.gt(0L));

//        query.where(board.title.contains("예시")); // where title like ...
//
//        //paging
//        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch(); // JQPLQuery 실행

        long count = query.fetchCount(); // count 쿼리

        return null;
    }


    /* @ManyToOne(fetch = FetchType.LAZY)로 설정되어 있으므로 기본적으로 지연 로딩(lazy loading)됩니다.
    지연 로딩은 관련 엔티티를 실제로 접근할 때 로드되도록 하는 전략입니다. 즉, Board 엔티티를 가져올 때 course와 user 필드는 초기에는 프록시 객체로 설정되고, 이 필드들에 접근할 때 데이터베이스에서 실제 데이터를 가져옵니다. */
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        // 검색 조건과 키워드가 있다면
        if( (types != null && types.length > 0) && keyword != null) {

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type : types){

                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.user.name.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);

        }

        query.where(board.bno.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    }
}
