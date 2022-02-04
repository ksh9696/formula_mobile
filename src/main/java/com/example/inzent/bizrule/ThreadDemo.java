package com.example.inzent.bizrule;


import com.example.inzent.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

//customized Thread
public class ThreadDemo implements Runnable {
    protected Log logger = LogFactory.getLog(getClass());
    private String id;
    private Thread mThread;

    private boolean mStop = false;
    private boolean mPause = false;
    private int processTest=0; //1일 때 임의의 값 저장, 2일 때 값 return
    //private RedisTemplate<String, Object> redisTemplate;
    private RedisService redisService;
    public ThreadDemo(String id, RedisService redisService) {
        this.id = id;
        this.redisService = redisService;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(id);
        logger.info(id+"Thread start");
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("js");

        try {
            while(!mStop) {
                synchronized(this) {
                    while(mPause) {
                        if(processTest == 1){
                            //redisService.exTask(id);
                            redisService.ckeckInputValue(id,engine);
                            mPause=false;
                            //this.pause();
                            //this.setProcessTest(0);
                        }else if(processTest == 2){
                            redisService.ckeckOutputValue(engine);
                            mPause=false;
                            //this.setProcessTest(0);
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
//
   // public synchronized void resume() {
   //     mPause = false;
   //     notify();
   // }

    public void setProcessTest(String id, int processTest){
        this.processTest = processTest;
        this.pause();
    }
}