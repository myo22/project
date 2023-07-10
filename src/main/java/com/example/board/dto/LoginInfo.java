package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//@AllArgsConstructor // 모든 필드에 대한 생성자가 자동으로 만들어진다.
public class LoginInfo {
    private Integer userId;
    private String email;
    private String name;
    private List<String> roles = new ArrayList<>(); // 선언과 동시에 초기화

    public LoginInfo(Integer userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    public void addRole(String roleName){ // 여기선 안쓰임;
        roles.add(roleName);
    }

//    public LoginInfo(Integer userId, String email, String name) {   ->> @AllArgsConstructord로 lombok이 알아서 생성자를 만들어줌.
//        this.userId = userId;
//        this.email = email;
//        this.name = name;
//    }
}
