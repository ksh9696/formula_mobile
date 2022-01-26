package com.example.inzent.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import java.util.UUID;

@Service
public class RedisService {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    //private final Logger logger = LoggerFactory.getLogger(RedisService.class);

    public String sign(String token) {
        //hashmap같은 key value 구조
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String id = UUID.randomUUID().toString().replace("-","");
        vop.set(token, id);
        return id;
    }

    public String checkId(String token) {
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String id = (String) vop.get(token);

        return id;
    }

    public String getConditionFile(String token, String conditionFile){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String id = this.checkId(token);

        if(id != null){
            vop.set(id+"_conditionFile",conditionFile);
        }
        System.out.println(vop.get(id+"_conditionFile"));

        return id+"_conditionFile";
    }

    public void exTask(String id){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(id+"_engineVal","id is "+id);
    }

    public void testWork(String id, ScriptEngine engine){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String engineVal = vop.get(id+"_engineVal").toString();
        System.out.println(engineVal);
        engine.put("engineVal",engineVal);
    }
    public void testWork2(String id, ScriptEngine engine){
        System.out.println(id+" engineVal : "+engine.get("engineVal").toString());
    }
}