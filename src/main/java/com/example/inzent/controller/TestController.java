package com.example.inzent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object test(){
        return "Hello World";
    }

}
