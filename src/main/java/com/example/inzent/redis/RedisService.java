package com.example.inzent.redis;

import com.example.inzent.bizrule.RedisKeyObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
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
}