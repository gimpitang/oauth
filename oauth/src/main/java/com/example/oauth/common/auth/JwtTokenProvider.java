package com.example.oauth.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
//    @Value("${jwt.expiration}")
//    int expiration;
//    @Value("${jwt.secretKey}")
//    String secretKey;
//    @Value("${jwt.expirationRt}")
//    int expirationRt;
//    @Value("${jwt.secretKeyRt}")
//    String secretKeyRt;

    private final String secretKey;
    private final int expiration;
    private Key SECRET_KEY;
//    private final String secretRefreshKey;
//    private final int expirationRt;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
    }

//      생성자가 호출되고 , 스프링빈이 만들어진 직후에 아래 메서드를 바로 실행하는 어노테이션


    public String createToken(String email, String role) {
//        claims는 jwt토큰의 payload 부분을 의미
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        //      claims는 사용자 정보(페이로드 정보)
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expiration*60*1000L)) // 30분 세팅
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }

//    public String createRefreshToken(String email, String role) {
//        Claims claims = Jwts.claims().setSubject(email);
//        claims.put("role", role);
//        Date now = new Date();
//        //      claims는 사용자 정보(페이로드 정보)
//        String token = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime()+expirationRt*60*1000L)) // 30분 세팅
//                .signWith(ENCRYPT_RT_SECRET_KEY)
//                .compact();
//        return token;
//    }
}
