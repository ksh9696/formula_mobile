package com.example.inzent.controller;

import com.example.inzent.bizrule.BizFileReader;
import com.example.inzent.bizrule.DemoList;
import com.example.inzent.bizrule.ThreadDemo;
import com.example.inzent.bizrule.ThreadService;
import com.example.inzent.jwt.JwtTokenProvider;
import com.example.inzent.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptEngine;
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

        //해당 thread engine에 공통함수 입력
        ThreadDemo demo = threadService.searchingThread(id);
        demo.setProcessTest(id,3);

        return token;
    }

    /*
     *redis 에 Condition.xml 파일 저장 메서드
     */
    @PostMapping(value = "/getFullConditionFile")
    public String getFullConditionFile(String scrnNm, HttpServletRequest request){
        //토큰값 얻기
        String token = jwtTokenProvider.resolveToken(request);

        //condition파일 읽고, id 값 가져오기
        String conditionFile = bizFileReader.bizFileReader(scrnNm);
        redisService.getFullConditionFile(token,conditionFile);

       ////파일 불러오기
       //JSONObject ob = redisService.makeConditionFile(conditionFile);

        return "getfile";
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
    @PostMapping(value = "/interrupThread")
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

    @PostMapping(value = "/getProcessId")
    public String getProcessId(HttpServletRequest request){
        JSONObject requiredItem = new JSONObject();
        requiredItem.put("PDT_GUD_MRKTNG_WRCNT_RQSD_YN","10");
        requiredItem.put("PDT_GUD_MRKTNG_WRCNT_RQSD_YN2","10");

        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);

        //해당 thread 필수 항목 넣어서 processId얻기
        ThreadDemo demo = threadService.searchingThread(id);

        demo.setRequiredItem(requiredItem);
        demo.setProcessTest(id,4);
        return "test";
    }
}


