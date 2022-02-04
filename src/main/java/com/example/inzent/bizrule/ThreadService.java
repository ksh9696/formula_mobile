package com.example.inzent.bizrule;

import com.example.inzent.InzentApplication;
import com.example.inzent.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class                                                                                                                                                                ThreadService {

    @Autowired
    @Qualifier("executor")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private DemoList demoList;


    /*
     *스레드 생성이벤트
     *@param id  UUID
     *@param redisService
     */
    public void executeThread(String id, RedisService redisService){
        ThreadDemo demo = new ThreadDemo(id, redisService);
        demoList.getDemoList().put(id,demo);
        executor.execute(demo);
    }

    /*
     *스레드 종료 이벤트
     *@param id  UUID
     */
    public void interrupThread(String id){
       Set<Thread>threadSet = Thread.getAllStackTraces().keySet();
       for(Thread t : threadSet){
           if(t.getName().equals(id)){
               t.interrupt();
               demoList.getDemoList().remove(id);
               log.info(id+"_Thread end");
           }
       }
    }

    /*
     *id값에 해당하는 스레드 찾는 메서드
     *@param id  UUID
     */
    public ThreadDemo searchingThread(String id){
        for( String key : demoList.getDemoList().keySet() ){
            if(key.equals(id)){
               ThreadDemo demo = demoList.getDemoList().get(key);
               return demo;
            }
        }
        return null;
    }
}


