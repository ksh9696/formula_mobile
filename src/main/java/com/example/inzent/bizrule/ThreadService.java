package com.example.inzent.bizrule;

import com.example.inzent.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class ThreadService {

    @Autowired
    @Qualifier("executor")
    private ThreadPoolTaskExecutor executor;

    //스레드 생성이벤트
    public void executeThread(String id, RedisService redisService){
        executor.execute(new ThreadDemo(id, redisService));
    }

    //스레드 종료 이벤트
    public void interrupThread(String id){
       Set<Thread>threadSet = Thread.getAllStackTraces().keySet();
       for(Thread t : threadSet){
           if(t.getName().equals(id)){
               t.interrupt();
               log.info(id+"_Thread 종료");
           }
       }
    }

    public Thread searchingThread(String id){
        Set<Thread>threadSet = Thread.getAllStackTraces().keySet();
        for(Thread t : threadSet){
            if(t.getName().equals(id)){
                return t;
            }
        }
        return null;
    }

}


