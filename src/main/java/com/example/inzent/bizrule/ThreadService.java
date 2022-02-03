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
public class ThreadService {

    @Autowired
    @Qualifier("executor")
    private ThreadPoolTaskExecutor executor;




    //스레드 생성이벤트
    public void executeThread(String id, RedisService redisService){
        ThreadDemo demo = new ThreadDemo(id, redisService);
        InzentApplication.demoList.put(id,demo);
        executor.execute(demo);
    }

    //스레드 종료 이벤트
    public void interrupThread(String id){
       Set<Thread>threadSet = Thread.getAllStackTraces().keySet();
       for(Thread t : threadSet){
           if(t.getName().equals(id)){
               t.interrupt();
               log.info(id+"_Thread end");
           }
       }
    }

    //id에 맞는 thread 찾기
   //public Thread searchingThread(String id){
   //    Set<Thread>threadSet = Thread.getAllStackTraces().keySet();
   //    Thread thread = new Thread();
   //    for(Thread t : threadSet){
   //        if(t.getName().equals(id)){
   //            thread = t;
   //        }
   //    }
   //    return thread;
   //}
    public ThreadDemo searchingThread(String id){
        for( String key : InzentApplication.demoList.keySet() ){
            if(key.equals(id)){
               ThreadDemo demo = InzentApplication.demoList.get(key);
               return demo;
            }
        }
        return null;
    }
}


