package com.example.board.security;

import com.example.board.Repository.MemberRepository;
import com.example.board.domain.Member;
import com.example.board.domain.MemberRole;
import com.example.board.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService { // DefaultOAuth2UserService는 카카오뿐만 아니라 구글, 페이스북 등의 다양한 소셜 로그인에서 사용 가능하다.

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("userRequest....");
        log.info(userRequest);

        log.info("oauth2 user....................................");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        // 이를 각각의 소셜 서비스에 맞게 clientRegistration.getClientName()를 통해 처리하도록 해야한다.
        String clientName = clientRegistration.getClientName();

        log.info("Name: " + clientName);
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;

        switch (clientName){
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
        }

        log.info("=====================================");
        log.info(email);
        log.info("=====================================");

        return generateDTO(email, paramMap);
    }

    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params) {

        Optional<Member> result = memberRepository.findByEmail(email);

        if (result.isEmpty()){
            // 회원 추가-- mid는 이메일주소/ 패스워드는1111
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1111"))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);

            // MemberSecurity 구성 및 반환
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(email, "1111", email, false, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params);

            return memberSecurityDTO;
        }else {
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(
                            member.getMid(),
                            member.getMpw(),
                            member.getEmail(),
                            member.isDel(),
                            member.isSocial(),
                            member.getRoleSet()
                                    .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name()))
                                    .collect(Collectors.toList())
                    );

            return memberSecurityDTO;
        }
    }

    private String getKakaoEmail(Map<String, Object> paramMap){
        log.info("KAKAO----------------------------------");

        Object value = paramMap.get("kakao_account");

        log.info(value);

        LinkedHashMap accountMap = (LinkedHashMap) value;

        String email = (String)accountMap.get("email");

        log.info("email..." + email);

        return email;
    }
}
