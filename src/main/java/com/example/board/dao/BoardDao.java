package com.example.board.dao;

import com.example.board.dto.BoardDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class BoardDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsertOperations insertBoard;

    // 생성자 주입. 스프링이 자동으로 HikariCP(datasource 구현체) Bean을 주입한다.
    public BoardDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertBoard = new SimpleJdbcInsert(dataSource)
                .withTableName("templates/board")
                .usingGeneratedKeyColumns("board_id"); // 자동으로 증가되는 id를 설정.
    }

    @Transactional
    public void addBoard(int userId, String title, String content) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setUserId(userId);
        boardDTO.setTitle(title);
        boardDTO.setContent(content);
        boardDTO.setRegDate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(boardDTO);
        insertBoard.execute(params);
    }

    @Transactional(readOnly = true)
    public int getTotalCount() {
        String sql = "select count(*) as total_count from board"; // 무조건 1건의 데이터가 나온다.
        Integer totalCount =  jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
        return totalCount.intValue();
    }

    @Transactional(readOnly = true)
    public List<BoardDTO> getBoards(int page) {
        // start는 0, 10, 20, 30 는 1page, 2page, 3page를 나타냄
        int start = (page -1) * 10;
        String sql ="select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name from board b, user u where b.user_id = u.user_id  limit :start, 10";
        RowMapper<BoardDTO> rowMapper = BeanPropertyRowMapper.newInstance(BoardDTO.class);
        List<BoardDTO> list = jdbcTemplate.query(sql, Map.of("start", start), rowMapper);
        return list;
    }

    @Transactional(readOnly = true)
    public BoardDTO getBoard(int boardId) {
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name, b.content from board b, user u where b.user_id = u.user_id  and b.board_id = :boardId";
        RowMapper<BoardDTO> rowMapper = BeanPropertyRowMapper.newInstance(BoardDTO.class);
//        SqlParameterSource params = new MapSqlParameterSource("boardId", boardId);
        BoardDTO boardDTO = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return boardDTO;
    }

    @Transactional
    public void updateViewCnt(int boardId) {
        String sql = "update board\n" +
                "set view_cnt = view_cnt +1\n" +
                "where board_id = :boardId";
        SqlParameterSource params = new MapSqlParameterSource("boardId", boardId);
        jdbcTemplate.update(sql, params);
    }


    @Transactional
    public void deleteBoard(int boardId){
        String sql = "delete from board where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void updateBoard(Long boardId, String title, String content){
        String sql = "update board\n" +
                "set title = :title , content = :content\n" +
                "where board_id = :boardId";
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBno(boardId);
        boardDTO.setTitle(title);
        boardDTO.setContent(content);
        SqlParameterSource params = new BeanPropertySqlParameterSource(boardDTO);
        jdbcTemplate.update(sql, params);
//        jdbcTemplate.update(sql, Map.of("boardId", boardId, "title", title, "content", content));
    }
}
