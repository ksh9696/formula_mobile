package com.example.inzent.bizrule;


import com.example.inzent.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

//customized Thread
public class ThreadDemo implements Runnable {
    protected Log logger = LogFactory.getLog(getClass());
    private String id;
    private String strScreenID;

    private boolean mStop = false;
    private boolean mPause = false;
    private int processTest=0; //1일 때 임의의 값 저장, 2일 때 값 return
    private JSONObject requiredItem = new JSONObject();


    ScriptEngineManager engineManager = new ScriptEngineManager();
    ScriptEngine engine = engineManager.getEngineByName("js");

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
        //ScriptEngineManager engineManager = new ScriptEngineManager();
        //ScriptEngine engine = engineManager.getEngineByName("js");

        try {
            while(!mStop) {
                synchronized(this) {
                    while(mPause) {
                        if (processTest == 1) {
                            //redis&engine에 저장 & 필수항목 리턴
                            redisService.inputCommonFunction(engine);
                            JSONObject ob = redisService.makeConditionFile(engine,strScreenID);
                            setRequiredItem(ob);
                            System.out.println(ob.toJSONString());
                            mPause = false;
                        } else if (processTest == 2) {
                            redisService.ckeckInputValue(id, engine);
                            mPause = false;
                        } else if (processTest == 3) {
                            redisService.ckeckOutputValue(engine);
                            mPause = false;
                        }else if(processTest == 4){
                            String processId = redisService.onPreProcess(id, engine, requiredItem);
                            System.out.println("TEST :" + processId);
                            mPause = false;
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
    public void setRequiredItem(JSONObject requiredItem){
        this.requiredItem = requiredItem;
    }
    public JSONObject getRequiredItem(){return requiredItem;}
    public void setStrScreenID(String strScreenID){
        this.strScreenID=strScreenID;
    }
    public String getStrScreenID(){return strScreenID;}
}