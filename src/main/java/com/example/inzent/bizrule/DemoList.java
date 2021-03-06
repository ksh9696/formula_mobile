package com.example.inzent.bizrule;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

//Thread List 전역으로 관리하기 위한 Component
@Data
@Component
public class DemoList {
    private  Map<String, ThreadDemo> demoList ;

    @PostConstruct
    void init(){
        demoList = new HashMap<>();
    }

}
