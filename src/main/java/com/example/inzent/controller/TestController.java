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
            id= redisService.getUUID(token);
            System.out.println("SIGN TEST : "+token);
            System.out.println("SIGN TEST : "+id);
        }
        if(id == null){
            id = redisService.checkId(token);
        }
        return "Hello World";
    }

    /*
     *request 에 대한 token발행 & UUID발급하여 redis에 저장 & thread start
     */
    @PostMapping(value = "/executeThread")
    public String executeThread() {
        //토큰 생성
        String token = jwtTokenProvider.createToken();
        //아이디 redis에 저장
        String id= redisService.getUUID(token);
        log.info("TOKEN :"+ token);
        log.info("ID :"+ id);

        //thread 생성
        threadService.executeThread(id, redisService);

        //스레드 생성시  list log 확인
        for(String key : demoList.getDemoList().keySet()){
            log.info("LIST CHECK : "+ key);
        }
        return "thread start";
    }

    /*
     *redis 에 Condition.xml 파일 저장 메서드
     *return String scrnNm 화면 변호
     */
    @RequestMapping(value = "/getFullConditionFile", method = RequestMethod.POST)
    public String getFullConditionFile(String scrnNm, HttpServletRequest request){
        //토큰값 얻기
        String token = jwtTokenProvider.resolveToken(request);

        //condition파일 읽고, id 값 가져오기
        String conditionFile = bizFileReader.bizFileReader(scrnNm);
        redisService.getFullConditionFile(token,conditionFile);

        return "get conditionfile";
    }

    /*
     *redis & thread에 임의의 값 input
     */
    @RequestMapping(value = "/checkInputValue", method = RequestMethod.POST)
    public String checkInputValue(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);

        ThreadDemo demo = threadService.searchingThread(id);
        demo.setProcessTest(id,1);


        return "input data";
    }

    /*
     *redis & thread에 임의의 값 output
     */
    @RequestMapping(value = "/checkOutputValue", method = RequestMethod.POST)
    public String checkOutputValue(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);
        //threadService.searchingThread(id);
       //Thread thread = threadService.searchingThread(id);
        ThreadDemo demo = threadService.searchingThread(id);
       //ThreadDemo demoThread = (ThreadDemo) thread;
       demo.setProcessTest(id,2);


        return "thread start";
    }

    /*
     *thread 종료 & 관련 redis데이터 삭제
     */
    @RequestMapping(value = "/interrupThread", method = RequestMethod.POST)
public String interrupThread(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);
        redisService.deleteRedisValue(token,id);
        threadService.interrupThread(id);
        return "thread interrup";
        }

    /*
     *ThreadDemo list확인
     */
    @PostMapping(value = "/checkDemoList")
    public String checkDemoList() {
        //스레드 생성시  list log 확인
        String fullDemoList = "";
        for (String key : demoList.getDemoList().keySet()) {
            fullDemoList += key+"/n";
        }
        return fullDemoList;
    }
}


