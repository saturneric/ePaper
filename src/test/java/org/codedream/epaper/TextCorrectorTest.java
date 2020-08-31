package org.codedream.epaper;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.nlp.AipNlp;
import org.codedream.epaper.component.EPSpringUtil;
import org.codedream.epaper.component.datamanager.TextCorrector;
import org.codedream.epaper.configure.NLPConfigure;
import org.codedream.epaper.configure.SingletonAipNlp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
public class TextCorrectorTest {

    @Resource
    EPSpringUtil epSpringUtil;

    @Test
    public void testCorrector() throws JSONException {

        /*
        TextCorrector textCorrector = epSpringUtil.getBean(TextCorrector.class);
        String text = "百度是一家人工只能公司";
        HashMap<String, Object> hashMap = textCorrector.correctText(text);
        System.out.println(hashMap.get("org_text").toString());
        System.out.println(hashMap.get("corr_text").toString());
        System.out.println(hashMap.get("org_pos").toString());
        System.out.println(hashMap.get("mod_pos").toString());
         */
        AipNlp client = SingletonAipNlp.getInstance();
        HashMap<String, Object> hashMap = new HashMap<>();
        String text = "一座美腻的城堡";
        System.out.println(client.ecnet(text, hashMap));
    }

    @Test
    public void littleTest() {

        HashSet<Character> hashSet = new HashSet<>();
        String text = "你是一个好人";
        String text2 = "你不是一个好人";
        List<Integer> poses1 = new ArrayList<>();
        List<Integer> poses2 = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            hashSet.add(text.charAt(i));
        }

        for (int i = 0; i < text2.length(); i++) {
            if (hashSet.contains(text2.charAt(i))) continue;
            poses1.add(i);
        }

        System.out.println(poses1.toString());
    }
}
