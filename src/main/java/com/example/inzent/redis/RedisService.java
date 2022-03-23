package com.example.inzent.redis;

import com.example.inzent.bizrule.CommonFunc;
import com.example.inzent.bizrule.RedisKeyObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.org.apache.xpath.internal.objects.XObject;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

@Slf4j
@Service
public class RedisService {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    //private final Logger logger = LoggerFactory.getLogger(RedisService.class);

    /*
     *UUID발급하여 redis에 저장하는 메서드
     *@param token
     *return id UUID
     */
    public String getUUID(String token) {
        //hashmap같은 key value 구조
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String id = UUID.randomUUID().toString().replace("-","");
        vop.set(token, id);

        log.info("TOKEN :"+ token);
        log.info("ID :"+ id);
        return id;
    }

    /*
     *redis에 해당 데이터 삭제 메서드
     *@param token
     *return id UUID
     */
    public void deleteRedisValue(String token, String id){
        redisTemplate.delete(token);
        redisTemplate.delete(id+ RedisKeyObject.CONDITION_FILE);
        redisTemplate.delete(id+ RedisKeyObject.ENGINE_VALUE);
    }

    /*
     *redis key 값에 저장된 token 에 해당하는 UUID 값 return하는 메서드
     *@param token
     *return id UUID값
     */
    public String checkId(String token) {
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        System.out.println("TEST :" +token);
        String id = (String) vop.get(token);

        return id;
    }

    /*
     *redis에 condition파일 저장 메서드
     *@param token
     *@param String conditionFile
     */
    public void getFullConditionFile(String token, String conditionFile){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String id = this.checkId(token);

        if(id != null){
            vop.set(id+"_conditionFile",conditionFile);
        }
        System.out.println(vop.get(id+"_conditionFile"));
        log.info("INPUT CONDITION FILE IN REDIS  : "+id+"_conditionFile");

    }

    /*
     *redis & ScriptEngine에 임의의 값 저장 메서드
     *@param String id
     *@param ScriptEngine engine
     *
     * redis(key : sdjflsjdfskdfjlas342lfl_engineVal, value : id is sdjflsjdfskdfjlas342lfl)
     * engine(key : engineVal, value : id is sdjflsjdfskdfjlas342lfl)
     */
    public void ckeckInputValue(String id, ScriptEngine engine){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        //redis에 저장
        vop.set(id+"_engineVal","id is "+id);
        String engineVal = vop.get(id+"_engineVal").toString();
        log.info("INPUT ID VALUE IN ENGINE : "+engineVal);
        //engine 에 저장
        engine.put("engineVal",engineVal);
    }

    /*
     *해당 thread에서 동작하는 ScriptEngine 값 확인 메서드
     *@param String id
     *@param ScriptEngine engine
     */
    public void ckeckOutputValue(ScriptEngine engine){
        log.info("RETURN ENGINE VALUE : "+engine.get("engineVal").toString());
    }

    public void inputCommonFunction(ScriptEngine engine){

       // //redis에서 condition파일
       // engine.put("processId", "");
       // try {
       //     engine.eval("function setProcessId(val){\r\n"
       //             + "		processId = val;\r\n"
       //             + "	}\r\n"
       //             + "	function getProcessId(){\r\n"
       //             + "		return processId;\r\n"
       //             + "	}");
       // } catch (ScriptException e) {
       //     e.printStackTrace();
       // }

        try {
            engine.eval(CommonFunc.COMMON_FUNCTION_SCRRIPT);
        } catch (ScriptException e) {
            e.printStackTrace();
            log.error("eval error");
        }
    }

    public JSONObject makeConditionFile(ScriptEngine engine,String strScreenID) {
        //JSONArray list = new JSONArray();
        JSONObject ob = new JSONObject();

        try {
            //1. condition load
            ClassPathResource resource = new ClassPathResource("file/TST_"+strScreenID+"_condition.xml");
            File file = resource.getFile();

            //빌더 팩토리 생성
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //빌더 팩토리로부터 빌더 생성
            DocumentBuilder db = dbf.newDocumentBuilder();
            //빌더를 통해 xml 문서를 파싱해서 Document 객체로 가져옴
            Document document = db.parse(file);
            //문서 구조 안정화
            document.getDocumentElement().normalize();

            NodeList EssentialScreenInfo  = document.getElementsByTagName("EssentialScreenInfo");

            for(int i=0;i<EssentialScreenInfo.getLength();i++) {
                Node nNode = EssentialScreenInfo.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    //ob.put("screenId", eElement.getAttribute("SCD_ID"));//화면 ID

                    //2. onpreopenprocess() engine에 넣기
                    String onPreOpenProcess = eElement.getElementsByTagName("FunctionScript").item(0).getTextContent();
                    //engine.put("onPreOpenProcess", onPreOpenProcess);
                    engine.eval(onPreOpenProcess);

                    NodeList simulator  = eElement.getChildNodes();
                    Node mNode = simulator.item(1);

                    if(mNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement2 = (Element) mNode;

                        String essential = eElement2.getAttribute("ESSENTIAL");
                        String[] essenArr = essential.split("@");

                        //3. json데이터 공통변수로 선언
                        for(String str:essenArr) {
                                String []essen = str.split("=");
                                engine.put(essen[0], "");
                                ob.put(essen[0], "");
                            }

                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        //4. 필수 항목 return
        return ob;
    }

    public String onPreProcess(String id, ScriptEngine engine, JSONObject requiredItem){
        Object processId = "";
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        Invocable invocable = (Invocable) engine;

        //필수 항목 engine에 저장
        Iterator<String> keys = requiredItem.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            engine.put(key, requiredItem.get(key));
        }
        try {
            //onpreprocess 호출
            invocable.invokeFunction("OnPreOpenProcess");
            //processId 얻기
            processId = invocable.invokeFunction("getProcessId");
            //vop.set(id+RedisKeyObject.PROCESS_ID,processId);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return processId.toString();
    }
}