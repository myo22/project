package com.example.board.dao;

import com.example.board.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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

// 데이터를 처리해 주는 역할. 직관적으로 알려줌
@Repository // spring이 관리하는 Bean이다.
public class UserDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private  final SimpleJdbcInsertOperations insertUser;

    public UserDao(DataSource dataSource) { // Hikari라는 데이터 소스를 구현하는 객체가 생성
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser = new SimpleJdbcInsert(dataSource).withTableName("user").usingGeneratedKeyColumns("user_id"); // 자동으로 증가되는 id를 설정.
    }

    // Spring JDBC를 이용한 코드.

    @Transactional // 두개의 쿼리가 실행되기 때문에 하나의 트랜잭션으로 실행해야 한다.
    public User addUser(String email, String name, String password){
        // Service에서 이미 트랜잭션이 시작했기 때문에, 그 트랜잭션에 포함된다. (새롭게 만들어지는것 X)
        // insert into user(email, name, password, regdate) values(:email, :name, :password, :regdate); #user_id는 자동생성 -> auto generation.
        // SELECT LAST_INSERT_ID(); 자동생성된 user_id를 바로 user에 담아서 리턴해준다.
        User user = new User();
        user.setName(name); // name 칼럼에 값을 넣어줌
        user.setEmail(email); // email
        user.setPassword(password); // password
        user.setRegdate(LocalDateTime.now()); // regdate
        SqlParameterSource params = new BeanPropertySqlParameterSource(user); // DTO에 있는 값을 자동으로  SqlParameterSource로 넣어주는 객체이다.
        // 기존에 사용하던 execute가 아닌 executeAndReturnKey을 사용하는 이유는 자동으로 생성된 ID를 가져온다.
        Number number = insertUser.executeAndReturnKey(params); // params자리에 String email, String name, String password들이 들어간다.
        int userId =  number.intValue();
        user.setUser_id(userId);
        return user;
    }

//    public int getLastInsertId(){ 별도로 만드는 것보다 INSERT 작업때 같이 들어가는게 좋다.
//        // SELECT LAST_INSERT_ID();
//        return 0;
//    }

    @Transactional
    public void mappingUserRole(int userId){
        // Service에서 이미 트랜잭션이 시작했기 때문에, 그 트랜잭션에 포함된다. (새롭게 만들어지는것 X)
        //  insert into user_role(user_id, role_id) values (?, 1); # 첫번째 물음표는 위에서 생성된 아이디를 아래에서 사용
        String sql = "insert into user_role(user_id, role_id) values (:userId, 1)";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        jdbcTemplate.update(sql,params);
    }

    @Transactional
    public User getUser(String email) {
        try {
            String sql = "select * from user where email = :email";
            SqlParameterSource params = new MapSqlParameterSource("email", email);
            // user_id => setUserId, email => setEmail ... 이렇게 만들어 준다.
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            User user =  jdbcTemplate.queryForObject(sql, params, rowMapper);
            return  user;
        }catch (Exception ex){
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        String sql = "select r.name from user_role ur, role r where ur.role_id = r.role_id and ur.user_id = :userId";
        List<String> roles  = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
            return rs.getString(1);
        });
        return roles;
    }
}

