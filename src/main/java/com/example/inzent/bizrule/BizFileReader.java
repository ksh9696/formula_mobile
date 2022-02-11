package com.example.inzent.bizrule;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class BizFileReader {

    /*
     *condition file 읽어 String으로 변환
     *@param scrnNm 화면번호
     * return String conditionFile
     *    ex)  if(PDT_GUD_MRKTNG_WRCNT_RQSD_YN == "10") {

	               setProcessId("TST_999999999_F_002"); //고객기본정보관리TEST

	            }
     */
    public String bizFileReader(String scrnNm) {
        //1. condition load
        File file = null;
        List<String> content = null;
        String conditonFile = null;
        try {
            ClassPathResource resource = new ClassPathResource("file/TST_" + scrnNm + "_condition.xml");
            file = resource.getFile();
            Path path = Paths.get(resource.getURI());
            content = Files.readAllLines(path);

            StringBuilder sb = new StringBuilder();
            for (String str : content) {
                sb.append(str + "\n").toString();
            }
            conditonFile = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conditonFile;
    }

}
