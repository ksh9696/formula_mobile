package com.example.inzent.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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
}