package com.example.inzent.controller;

import com.example.inzent.bizrule.BizFileReader;
import com.example.inzent.bizrule.DemoList;
import com.example.inzent.bizrule.ThreadDemo;
import com.example.inzent.bizrule.ThreadService;
import com.example.inzent.jwt.JwtTokenProvider;
import com.example.inzent.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisService redisService;
    @Autowired
    BizFileReader bizFileReader;
    @Autowired
    ThreadService threadService;
    @Autowired
    DemoList demoList;


    @GetMapping(value = "/")
    public Object test(HttpServletRequest request){
        String id=null;
        //토큰값 얻기
        String token = jwtTokenProvider.resolveToken(request);
        if(token == null){
            //토큰 생성
            token = jwtTokenProvider.createToken();
            //아이디 redis에 저장
            id= redisService.sign(token);
            System.out.println("SIGN TEST : "+token);
            System.out.println("SIGN TEST : "+id);
        }
        if(id == null){
            id = redisService.checkId(token);
        }
        return "Hello World";
    }

    @PostMapping(value = "/sign")
    public String sign() {
       //토큰 생성
        String token = jwtTokenProvider.createToken();
        //아이디 redis에 저장
        String id= redisService.sign(token);
        log.info("TOKEN :"+ token);
        log.info("ID :"+ id);
        return token;
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

    /*
    *@param scrnNm 화면번호
    *@HttpServletRequest request
    */
    @RequestMapping(value = "/getConditionFile", method = RequestMethod.POST)
    public String getFileInfo(String scrnNm, HttpServletRequest request){
        //토큰값 얻기
        String token = jwtTokenProvider.resolveToken(request);

        //condition파일 읽고, id 값 가져오기
        String conditionFile = bizFileReader.bizFileReader(scrnNm);
        String id_conditionFile = redisService.getConditionFile(token,conditionFile);

        return id_conditionFile;
    }

    @RequestMapping(value = "/executeThread", method = RequestMethod.POST)
    public String executeThread(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);
        threadService.executeThread(id, redisService);

        //스레드 생성시  list log 확인
        for(String key : demoList.getDemoList().keySet()){
            log.info("LIST CHECK : "+ key);
        }
        return "thread start";
    }

    @RequestMapping(value = "/checkEngineVal1", method = RequestMethod.POST)
    public String checkEngineVal1(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);

        ThreadDemo demo = threadService.searchingThread(id);
        demo.setProcessTest(id,1);


        return "thread start";
    }

    @RequestMapping(value = "/checkEngineVal2", method = RequestMethod.POST)
    public String checkEngineVal2(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);
        //threadService.searchingThread(id);
       //Thread thread = threadService.searchingThread(id);
        ThreadDemo demo = threadService.searchingThread(id);
       //ThreadDemo demoThread = (ThreadDemo) thread;
       demo.setProcessTest(id,2);


        return "thread start";
    }

    @RequestMapping(value = "/interrupThread", method = RequestMethod.POST)
public String interrupThread(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);
        redisService.deleteRedisValue(token,id);
        threadService.interrupThread(id);
        return "thread interrup";
        }
        }


