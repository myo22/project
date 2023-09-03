package com.example.board.Repository;

import com.example.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> { // 게시물같은 경우는 글을 많이 쓸 수 있어서 Long으로 하는것이 좋다.
    // JPQL을 사용할 수 있다.
    // JPQL은 SQL과 모양이 비슷하지만, SQL이 아니다.
    // JPQL은 객체지향 언어이다.
    // JPQL에서 from 뒤에 있는 것은 엔티티 이름
    // Board 엔티티들을 조회하라. 엔티티는 결국 table과 관계가 있다.
    @Query(value = "select b from Board b join fetch b.user") // 성능을 향상시키기 위해 fetch조인을 제공한다. -> 1 + N 문제 해결.
    List<Board> getBoards();

//    // 일반 조인, Board가 가진 속성이 아니고 별도의 엔티티를 이용해서 조인한 것. -> 1 + N 문제는 해결되지 않음.
//    @Query(value = "select b from Board b join fetch User u on b.user.userId = u.userId")
//    List<Board> getBoards();

    // 페이징 처리로 가지고 오는데 날짜를 desc로 정렬해서 가지고 온다.
    Page<Board> findByOrderByRegdateDesc(Pageable pageable);

    @Query(value = "select count(b) from Board b")
    Long getBoardCount();

    // 테이블이 4가지면 조인 조건은 테이블수 - 1 이다.
    // select * from board b, user u, user_role ur, role r where b.user_id = u.user_id and u.user_id = ur.user_id and ur.role_id = r.role_id and r.name = "ROLE_ADMIN";


    // 관리자가 쓴 글만 목록을 구하는 JPQL을 작성하시오. 이게 가능할가요?
    // @Query(value = "select b from Board b, User u, Role r where b.user.userId = u.userId and r.roleId = 2") 이렇게 처음에 쓰고 틀렸다가 아래로 고쳤다.
    @Query(value = "select b from Board b join fetch b.user u join u.roles r where r.roleId = 2")
    List<Board> getBoard();

    // 테이블이 4가지면 조인 조건은 테이블수 - 1 이다.
    // select * from board b, user u, user_role ur, role r where b.user_id = u.user_id and u.user_id = ur.user_id and ur.role_id = r.role_id and r.name = "ROLE_ADMIN";
//    @Query(value = "select b from Board b join fetch b.user u join u.roles r where r.name = :roleName") // jpql에서는 물음표를 사용하면 안된다.
//    List<Board> getBoards(@Param("roleName") String roleName);
    // Board b join b.user 이 부분은 SQL로 따지면 b.user_id = u.user_id 와 같다.

    // fetch를 빼고도 Alias를 이용해서도 동일하게 가능하다.
    @Query(value = "select b, u from Board b join b.user u join u.roles r where r.name = :roleName")
    List<Board> getBoards(@Param("roleName") String roleName);

}
