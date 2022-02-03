package com.example.inzent;

import com.example.inzent.bizrule.ThreadDemo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class InzentApplication {

    public static Map<String, ThreadDemo> demoList = new HashMap<>();
    public static void main(String[] args) {

        SpringApplication.run(InzentApplication.class, args);
    }
}
