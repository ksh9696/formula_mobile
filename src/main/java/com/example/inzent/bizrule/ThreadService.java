package com.example.inzent.bizrule;

import com.example.inzent.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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



class ThreadDemo implements Runnable {
    private String id;
    private Thread mThread;

    private boolean mStop = false;
    private boolean mPause = false;
    private int processTest=0;
    //private RedisTemplate<String, Object> redisTemplate;
    private RedisService redisService;
    public ThreadDemo(String id, RedisService redisService) {
        this.id = id;
        this.redisService = redisService;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(id);
        System.out.println(id+" thread 실행중");
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("js");

        try {
            while(!mStop) {
                synchronized(this) {
                    //redis 확인
                    this.setProcessTest(1);
                    while(mPause) {
                        if(processTest == 1){
                            //처리완료후
                            // 레디스 저장/
                            redisService.exTask(id);
                            redisService.testWork(id,engine);
                            //ValueOperations<String, Object> vop = redisTemplate.opsForValue();
                            //String engineVal = vop.get(id+"_engineVal").toString();
                            //System.out.println(engineVal);
                            //engine.put("engineVal",engineVal);
                            //System.out.println(id+" engineVal : "+engine.get("engineVal").toString());
                            //스레드 임시 정지
                            this.pause();
                            this.setProcessTest(0);
                        }else{
                            wait();
                        }
                    }
                }

                // TODO: insert your work code

            }
        } catch(InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void pause() {
        mPause = true;
    }

    public synchronized void resume() {
        mPause = false;
        notify();
    }

    public synchronized void setProcessTest(int processTest){
        this.processTest = processTest;
        this.pause();
        //notify();
    }
}