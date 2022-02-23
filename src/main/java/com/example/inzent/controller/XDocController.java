package com.example.inzent.controller;


import com.example.inzent.bizrule.BizFileReader;
import com.example.inzent.bizrule.DemoList;
import com.example.inzent.bizrule.ThreadDemo;
import com.example.inzent.bizrule.ThreadService;
import com.example.inzent.jwt.JwtTokenProvider;
import com.example.inzent.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class XDocController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BizFileReader bizFileReader;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private DemoList demoList;



    /*
    XDoc 초기화 , DLL 로드 후 최초 1회 실행
    IsClientCall : 서식 Client 호출여부 ( false인 경우 시뮬레이터에서 호출 )
    strSystemMode : 시스템 모드 ( R : 운영 , D : 개발 )
     */
    @PostMapping(value = "/XDocInit")
    public String XDocInit(boolean isClientCall , String strSystemMode ){
        //XDoc 초기화

        //토큰 생성
        String token = jwtTokenProvider.createToken();
        //아이디 redis에 저장
        String id= redisService.getUUID(token);
        log.info("TOKEN :"+ token);
        log.info("ID :"+ id);

        //thread 생성
        threadService.executeThread(id, redisService);

        return token;
    }


    /*
    XDoc 초기화 이후 한번 호출
    이벤트 처리( 메시지)를 위해 DLL에서 Client를 호출를 위해 콜백함수 지정
    fCallback : 콜백함수정보
     */
    @PostMapping(value = "/XDocCallBack")
    public void XDocCallBack(String fCallback){

    }

    /*
     * XDoc 할당값 제거 , DLL 종료시 1회 실행
     * */
    @PostMapping(value = "/XDocClose")
    public void XDocClose(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);
        redisService.deleteRedisValue(token,id);
        threadService.interrupThread(id);
    }

    /*
    선택한 화면의 bizRule 정보를 로딩한다
    strScreenID strScreenID : 로딩하려는 Rule의 화면번호
     return jsonDefaultControlInfo 필수항목 정보(json)
     */
    @PostMapping(value = "/XDocLoadScreen")
    public JSONObject XDocLoadScreen(String strScreenID, HttpServletRequest request) throws InterruptedException {
        JSONObject jsonDefaultControlInfo = new JSONObject();
        //토큰값 얻기
        String token = jwtTokenProvider.resolveToken(request);
        String id = redisService.checkId(token);

        //해당 thread engine에 공통함수 입력 & condition.xml load
        ThreadDemo demo = threadService.searchingThread(id);
        demo.setStrScreenID(strScreenID);
        demo.setProcessTest(id,1);

        boolean flag = true;
        while (flag){
            if(!demo.getRequiredItem().toString().equals("{}"))
                flag = false;
        }
        jsonDefaultControlInfo = demo.getRequiredItem();

       //
       ////condition파일 읽고, id 값 가져오기
       //String conditionFile = bizFileReader.bizFileReader(scrnNm);
       //redisService.getFullConditionFile(token,conditionFile);
       return jsonDefaultControlInfo;
    }

    /*
    필수항목 값으로 비즈룰 프로세스 선택 이벤트( OnPreOpenProcess ) 호출 후
    선택된 프로세스 정보 로딩
    jsonDefaultControlInfo : 필수항목 값 정보
     return 프로세스 ID (JSON)
     */
    @PostMapping(value = "/XDocLoadBizProcess")
    public JSONObject XDocLoadBizProcess(JSONObject jsonDefaultControllInfo){
        JSONObject processId = new JSONObject();
        return processId;
    }

    /*
    선택된 프로세스의 필수서식, 부가서식, 서식 선택 이벤트 (OnSelectForm) 호출 후 선택된 서식정보 리스트를 제공함
    structFormInfo : 서식정보 구조체
    nFormCount : 서식정보의 개수
     */
    @PostMapping(value = "/XDocGetFormList")
    public void XDocGetFormList(JSONArray structFormInfo, int nFormCount){

    }

    /*
    선택된 프로세스의 화면-서식간의 매핑정보 리스트를 제공함
    nDirection :  매핑방향 , SEND ( 단말->서식) , RECEIVE 서식->단말 )
    structMappingItem : 매핑정보 구조체
    nMappingCount : 매핑정보 개수
     */
    @PostMapping(value = "/XDocGetMappingList")
    public void XDocGetMappingList(String nDirection, JSONArray structmappingItem, int nMappingCount){

    }

    /*
    선택된 프로세스의 등록된 화면컨트롤(객체)정보 리스트를 제공함
    structControlInfo :  컨트롤정보
    nControlCount : 컨트롤정보 개수
     */
    @PostMapping(value = "/XDocGetScreenControlList")
    public void XDocGetScreenControlList(JSONArray structControlInfo, int nControlCount){

    }

    /*
    선택된 프로세스의 등록된 서식컨트롤(객체)정보 리스틀 제공함
    strFormId : 서석ID
    structControlInfo :  컨트롤정보
    nControlCount : 컨트롤정보 개수
     */
    @PostMapping(value = "/XDocGetFormControlList")
    public void XDocGetFormControlList(String strFormId, JSONArray structControlInfo, int nControlCount){

    }

    /*
    화면(단말)로부터 전달 받은 컨트롤(객체) 값을 bizRule에 전달
    화면 -> 서식 매핑 이전에 호출 해야하는 함수
    jsonControlData : 화면컨트롤(객체)의 값 리스트(JSON)
     */
    @PostMapping(value = "/XDocSetScreenData")
    public void XDocSetScreenData(JSONArray jsonControlData){

    }

    /*
    화면(단말)로부터 전달 받은 컨트롤(객체) 값을 bizRule에 전달
    서식 -> 화면  매핑 이전에 호출 해야 하는 함수
    strFormID : 설정하려는 서식 ID
    jsonControlData : 서식 컨트롤(객체)의 값 리스트(JSON
     */
    @PostMapping(value = "/XDocSetFormData")
    public void XDocSetFormData(String strFormID, JSONArray jsonControlData){

    }

    /*
    매핑 룰관리에서 OnMapping 이벤트에 스크립트 처리 여부를 체크해서 존재하는 경우
    해당 스크립트 실행하여 해당 결과값을 리턴 시킴
    OnMapping 이벤트에선 내부함수는 alert 함수만 사용가능
    nDirection : 매핑 방향 , 화면->서식 or 서식->화면
    strSendControlId : 매핑상 보내지는 컨트롤 ID
    strReceiveControlId : 매핑상 받는 컨트롤 ID
    strControlValue : 전달되는 값
    strFormId : 서식ID ( 매핑방향에 상관없음 )
    return 스크립트 처리된 결과 값
     */
    @PostMapping(value = "/XDocOnMappingEvent")
    public void XDocOnMappingEvent(
            String nDirection,
            String strSendControlId,
            String strReceiveControlId,
            String strControlValue,
            String strFormId
    ){

    }

    /*
    모든 컨트롤(객체)의 매핑이 종료되면 호출하는 함수
    매핑 룰관리에서 OnAfter 이벤트에 스크립트 처리 여부를 체크해서 존재하는 경우 해당 스크립트 실행하여 처리 결과를 생성 시킴
    호출후 이벤트 처리를 위해 eventProcess ( 제공되는 함수 이용) 처리
     */
    @PostMapping(value = "/XDocOnAfterMappingEvent")
    public JSONObject XDocOnAfterMappingEvent(){
        JSONObject result = new JSONObject();
        return result;
    }

    /*
    서식의 Change 이벤트가 발생되면 호출하는 함수
    서식에서 전달받은 정보를 그대로 전달 처리
    매핑 룰관리에서 Change 이벤트에 스크립트 처리 여부를 체크해서 존재하는 경우 해당 스크립트 실행하여 처리 결과를 생성 시킴
    호출후 이벤트 처리를 위해 eventProcess ( 제공되는 함수 이용) 처리
    strFormId : Change 이벤트가 발생한 서식 ID
    strControlId : Change 이벤트가 발생한 서식의 컨트롤(객체) ID
    strValue : Change 이벤트가 발생한 컨트롤(객체) 의 변경된 값
     */
    @PostMapping(value = "/XDocOnChangeEvent")
    public JSONObject XDocOnChangeEvent(String strFormId, String strControlId, String strValue){
        JSONObject result = new JSONObject();
        return result;
    }
}
