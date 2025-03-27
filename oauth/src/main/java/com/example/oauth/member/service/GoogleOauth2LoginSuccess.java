package com.example.oauth.member.service;

import com.example.oauth.common.auth.JwtTokenProvider;
import com.example.oauth.member.domain.Member;
import com.example.oauth.member.domain.SocialType;
import com.example.oauth.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

@Service
public class GoogleOauth2LoginSuccess extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public GoogleOauth2LoginSuccess(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, org.springframework.security.core.Authentication authentication) throws java.io.IOException, jakarta.servlet.ServletException {

//        oauth프로필 추출
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String openId = oAuth2User.getAttribute("sub");
            String email = oAuth2User.getAttribute("email");

//        회원가입 여부 확인
        Member member = memberRepository.findBySocialId(openId).orElse(null);
        if(member==null){
            member = Member.builder()
                    .socialType(SocialType.GOOGLE)
                    .socialId(openId)
                    .email(email)
                    .build();
            memberRepository.save(member);
        }
//        jwt 토큰 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

//        클라이언트 redirect 방식으로 토큰 전달(보안이 안좋음)
//        response.sendRedirect("http://localhost:3000?token"+jwtToken);
        Cookie jwtCookie = new Cookie("token", jwtToken);
        jwtCookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        response.addCookie(jwtCookie);
        response.sendRedirect("http://localhost:3000");



    }

}
