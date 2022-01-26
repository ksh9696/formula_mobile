package com.example.inzent.bizrule;


import com.example.inzent.redis.RedisService;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ThreadDemo implements Runnable {
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
                    //this.setProcessTest(1);
                    while(mPause) {
                        if(processTest == 1){
                            redisService.exTask(id);
                            redisService.testWork(id,engine);
                            System.out.println("");
                            this.pause();
                            //this.setProcessTest(0);
                        }else if(processTest == 2){
                            redisService.testWork2(id,engine);
                            this.pause();
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

    public synchronized void resume() {
        mPause = false;
        notify();
    }

    public void setProcessTest(String id, int processTest){
        this.processTest = processTest;
        this.id = id;
        //this.pause();
        notify();
    }
}