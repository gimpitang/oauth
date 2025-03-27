package com.example.oauth.common.config;

import com.example.oauth.common.auth.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtTokenFilter authFilter;

    public SecurityConfig(JwtTokenFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public PasswordEncoder makePassword(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                //      spring security 에서 cors 정책 지정
                .cors(c -> c.configurationSource(corsConfiguration()))
                .csrf(AbstractHttpConfigurer::disable) //       csrf 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) //      http basic 보안방식 비뢀성화
                //      세션로그인 방식 사용하지 않는다는 것을 의미
                .sessionManagement(s-> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //      .authenticated() : 모든 요청에 대해서 Authentication 객체가 생성되기를 요구.
                .authorizeHttpRequests(a->a.requestMatchers
                        ("/member/create","member/doLogin","member/refresh-token",
                                "product/list","member/google/doLogin","member/kakao/doLogin")
                        .permitAll().anyRequest().authenticated())
                //      token을 검증하고, token을 통해 Authentication 생성
                //      UsernamePasswordAuthenticationFilter 이 클래스에서 폼 로그인 인증을 처리한다.
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("*"));            //     모든 HTTP메서드(get, post, patch 등) 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));            //     모든 헤더 허용
        configuration.setAllowCredentials(true);                        //     자격증명 허용(인증처리 하겠다는 뜻)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  //      모든 url 패턴에 대해 cors 설정 적용

        return source;
    }
}
