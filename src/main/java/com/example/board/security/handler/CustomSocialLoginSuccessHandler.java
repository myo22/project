package com.example.board.security.handler;

import com.example.board.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        log.info("-----------------------------------------------");
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess.......");
        log.info(authentication.getPrincipal());

        // 스프링 시큐리티와 OAuth2 로그인 과정에서 인증된 사용자의 정보를 MemberSecurityDTO로 설정했기 때문
        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal(); // 인증된 사용자의 주요 정보 반환

        String encodePw = memberSecurityDTO.getMpw();

        // 소셜 로그인이고 회원의 패스워드가 1111

        if (memberSecurityDTO.isSocial()
                && (memberSecurityDTO.getMpw().equals("1111")
                    || passwordEncoder.matches("1111", memberSecurityDTO.getMpw()) // 암호화된 비밀번호와 원래 비밀번호와 일치하는지 확인하는것.
        )) {
            log.info("Should Change Password");

            log.info("Redirect to Member Modify");
            response.sendRedirect("/member/modify");

            return;
        } else {

            response.sendRedirect("/");
        }
    }
}
