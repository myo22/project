package com.example.board.Repository;

import com.example.board.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Spring Data JPA Repository를 완성. <- 앞에서 JDBC를 이용해서 만든 DAO와 거의 유사한 것이다.
// 보통 인터페이스를 선언하면? 인터페이스를 구현하는 클래스를 작성해야지.라고 생각한다.
// Spring Data JPA는 마법을 부린다 -> UserRepository를 구현하는 Bean을 자동으로 만들어준다.
public interface UserRepository extends JpaRepository<User, Integer> {

//    @Query("SELECT u FROM User u JOIN u.courses c WHERE c.courseId = :courseId")
//    List<User> findByCourseId(@Param("courseId") int courseId);



    // Optional이라 값이없으면 Optional User가 나온다.
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name); // query method - Spring Data JPA가 제공
    // 동명이인이 있다면 List<User> findByName(String name); 이렇게 만들어야하고 동명이인이 없다는 가정하에 위처럼 만든 것.

    // where name = ? and email = ?
    Optional<User> findByNameAndEmail(String name, String email);

    // where name like ? or email = ?
    List<User> findByNameOrEmail(String name, String email);

    // where user_id between ? and ?
    List<User> findByUserIdBetween(int startUserId, int endUserId);

    // where user_id < ?
    List<User> findByUserIdLessThan(int userId);

    // where user_id <= ?
    List<User> findByUserIdLessThanEqual(int userId);

    // where user_id > ?
    List<User> findByUserIdGreaterThan(int userId);

    // where user_id >= ?
    List<User> findByUserIdGreaterThanEqual(int userId);

    // where regdate > ?
    List<User> findByRegdateAfter(LocalDateTime day);

    // where regdate < ?
    List<User> findByRegdateBefore(LocalDateTime day);

    // where name is null
    List<User> findByNameIsNull();

    // where name is not null
    List<User> findByNameIsNotNull();

    // where name like ?
    List<User> findByNameLike(String name);

    // where name like '입력한값%'
    List<User> findByNameStartingWith(String name);

    // where name like '%입력한값'
    List<User> findByNameEndingWith(String name);

    // where name like '%입력한값%'
    List<User> findByNameContaining(String name);

    // order by name asc
    List<User> findByOrderByNameAsc();

    // order by name desc
    List<User> findByOrderByNameDesc();

    // where regdate > ? order by name desc
    List<User> findByRegdateAfterOrderByNameDesc(LocalDateTime day);

    // where name <>? (단, null은 나오지 않으니 주의해야한다.)
    List<User> findByNameNot(String name);

    // where user_id in( ..... )
    List<User> findByUserIdIn(Collection<Integer> userIds);

    // where user_id not in( ..... )
    List<User> findByUserIdNotIn(Collection<Integer> userIds);

//    // where flag = true
//    List<User> findByFlagTrue(); // List<User> findByFlagTrue(); = List<User> findByFlag(boolean flag);
//
//    // where flag = false
//    List<User> findByFlagFalse();

    // select count(*) from user2
    Long countBy();

    // select count(*) from user2 where name like?
    Long countByNameLike(String name);

    // where email = ?
    boolean existsByEmail(String email);

    // select * from user2 where name = ?
    // select한 건수만큼 delete from user2 where user_id = ?
    int deleteByName(String name);

    // select distinct * from user2 where name = ?
    List<User> findDistinctByName(String name);

    // 결과중 2건만 - select * from user2 limit 2
    List<User> findFirst2By();
    List<User> findTop2By();

    // 페이징 처리에 대한 정보를 받아들인다.
    Page<User> findBy(Pageable pageable);
    Page<User> findByName(String name, Pageable pageable);

}

// Page<Board> findBy(Pageable pageable);
// Page<Board> findByName(String name, Pageable pageable);
// Page<Board> findByTitleContaining(String title, Pageable pageable);
// Page<Board> findByContentContaining(String content, Pageable pageable);
// Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);