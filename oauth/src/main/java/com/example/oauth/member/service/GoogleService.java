package com.example.oauth.member.service;

import com.example.oauth.member.dto.AccessTokenDto;
import com.example.oauth.member.dto.GoogleProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.sql.SQLOutput;

@Service
public class GoogleService {
    @Value("${oauth.google.client-id}")
    private String googleClientId;
    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public AccessTokenDto getAccessToken(String code){
//        인가코드, clientId, client_secret, redirect_uri, grant_type
//        인가코드의 경우 프론트에서 받는다면 프론트에서 받아내야함.
//        clientId, client_secret, redirect_uri 이 세개는 중요+변경될 수 있는 정보이므로 yml 파일에 넣어서 꺼내오는 식으로 할거임

//        Spring6부터 RestTemplate 비추천 상태(곧 사라짐)이므로 대신 RestClient 사용
        RestClient restClient = RestClient.create();

//        MultiValueMap을 통해 자동으로 form-data 형식으로 body 조립 가능
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id",googleClientId );
        params.add("client_secret",googleClientSecret );
        params.add("redirect_uri",googleRedirectUri );
        params.add("grant_type","authorization_code");

//        ResponseEntity<String> response = restClient.post()
        ResponseEntity<AccessTokenDto> response = restClient.post()
//                토큰을 받아오는 uri(정보에 들어간 값을 사용)
                .uri("https://oauth2.googleapis.com/token")
//                content-type을 받아서 헤더에 넣는건가...? 암튼 뒤에는 json형식이 아닌 formdata형식으로 값을 받는다는 뜻임
                .header("Content-Type", "application/x-www-form-urlencoded")
//                body값에 들어가는 것 인가코드, clientId, client_secret, redirect_uri, grant_type 이 5가지는 직접 조립해서 넣어줘도 됨.
//                ?code=xxxx&clidnt_id=yyyy&...
//                이걸 위해서 윗쪽에 multimap이라는 것을 만들었음.
                .body(params)
//                retrieve는 응답의 body 값만 추출 하는 것
                .retrieve()
//                .toEntity(String.class);
                .toEntity(AccessTokenDto.class);

        System.out.println("응답 accessToken json " + response.getBody());


        return response.getBody();
    }
    public GoogleProfileDto getGoogleProfile(String token){

        RestClient restClient = RestClient.create();

        ResponseEntity<GoogleProfileDto> response = restClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer "+token)
                .retrieve()
                .toEntity(GoogleProfileDto.class);

        System.out.println("profile Json: "+response);

        return response.getBody();
    }
}
