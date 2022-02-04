package com.example.inzent.bizrule;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class BizFileReader {

    //화면 번호에 해당하는 파일 읽기

    /*
     *condition file 읽어 String으로 변환
     *@param scrnNm 화면번호
     */
    public String bizFileReader(String scrnNm){
        //1. condition load
        File file=null;
        List<String> content=null;
        String conditonFile=null;
        try {
            ClassPathResource resource = new ClassPathResource("file/TST_"+scrnNm+"_condition.xml");
            file = resource.getFile();
            Path path = Paths.get(resource.getURI());
            content = Files.readAllLines(path);

            StringBuilder sb = new StringBuilder();
            for(String str:content){
               sb.append(str+"\n").toString();
            }
            conditonFile = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

       return conditonFile;
    }
}
