package com.example.inzent.controller;

import com.example.inzent.jwt.JwtTokenProvider;
import com.example.inzent.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class TestController {
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisService redisService;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object test(){
        return "Hello World";
    }

    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    public String sign() {
       //토큰 생성
        String token = jwtTokenProvider.createToken();
        //아이디 redis에 저장
        String id= redisService.sign(token);
        System.out.println("SIGN TEST : "+token);
        System.out.println("SIGN TEST : "+id);

        return "SUCCESS";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String accessUser(HttpServletRequest request){
        //토큰값 얻기
        String token = jwtTokenProvider.resolveToken(request);
        if(jwtTokenProvider.validateToken(token)){
            //id값 얻기
            String id = redisService.checkId(token);
            if(id != null){
                System.out.println("LOGIN TEST : "+id);
                return "SUCCESS";
            }
        }
        return "redirect 로그를 확인하세요";

    }
}
