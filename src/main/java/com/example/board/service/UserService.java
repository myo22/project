package com.example.board.service;

import com.example.board.Repository.RoleRepository;
import com.example.board.Repository.UserRepository;
import com.example.board.dao.UserDao;
import com.example.board.domain.Role;
import com.example.board.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// 트랜잭션 단위로 실행될 메소드를 선언하고 있는 클래스
@Service // 스프링이 관래하는 Bean
@RequiredArgsConstructor // lombok이 final 필드를 초기화하는 생성자를 자동으로 생성한다.
public class UserService {

    private final UserRepository userRepository; // final 변수는 반드시 생성자를 통해 초기화 해저야한다. final 변수가 여러개면 매개변수에 여러개 들어가 줘야한다.
    private final RoleRepository roleRepository;

    // Spring이 UserService를 Bean으로 생성할때 생성자를 이용해 생성을 하는데, 이때 UserDao Bean이 있는지 보고
    // 그 빈을 주입한다. 생성자 주입.
//    public UserService(UserDao userDao){   ->>   @RequiredArgsConstructor로 자동처리
//        this.userDao = userDao;
//  }

    // 보통 서비스에서는 @Transactional 을 붙여서 하나의 트랜잭션으로 처리하게 한다.
    // Spring Boot는 트랜잭션을 처리해주는 트랜잭션 관리자를 가지고 있다.
    @Transactional
    public User addUser(String name, String email, String password) {
        // 트랜잭션이 여기서 시작된다.
        Optional<User> optionalUser = userRepository.findByEmail(email); // User user1 = userRepository.findByEmail(email).orElsethrow()도 가능하다.
        if (optionalUser.isPresent()){ // 이메일 중복 검사
            throw new RuntimeException("이미 가입된 이메일 입니다.");
        }

        Role role = roleRepository.findByName("ROLE_USER").get();
        User user = new User();
        user.setPassword(password);
        user.setRoles(Set.of(role));
        user.setEmail(email);
        user.setName(name);
        user = userRepository.save(user); // user가 리턴 되는 것이다. - >  userDao.getLastInsertId(); // 유저의 ID값을 받아옴.
        return user;
        // 트랜잭션이 끝난다.
    }

    @Transactional
    public User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow();
    }

    // get Roles는 위에서 getUser정보를 달라하면 이미 User정보안에 Role권한이 있을거라 필요가 없다.
    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        Set<Role> roles = userRepository.findById(userId).orElseThrow().getRoles();
        List<String> list = new ArrayList<>();
        for(Role role : roles){
            list.add(role.getName());
        }
        return list;
    }
}
