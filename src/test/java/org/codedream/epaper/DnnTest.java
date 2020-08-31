package org.codedream.epaper;

import com.baidu.aip.nlp.AipNlp;
import org.codedream.epaper.component.datamanager.SentenceSmoothnessGetter;
import org.codedream.epaper.configure.SingletonAipNlp;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class DnnTest {

    @Test
    public void testDnn() throws JSONException {

        String text = "阿斯顿发射点";
        AipNlp client = SingletonAipNlp.getInstance();
        HashMap<String, Object> options = new HashMap<>();
        JSONObject res = client.dnnlmCn(text, options);


        System.out.println(res.get("ppl"));
    }

    @Test
    public void testSingleton() {
        Thread2[] ThreadArr = new Thread2[10];
        for (int i = 0; i < ThreadArr.length; i++) {
            ThreadArr[i] = new Thread2();
            ThreadArr[i].start();
        }
    }

    @Test
    public void testSentenceSmoothnessGetter() {

        String text = "该项目的软件产品的客户端功能主要分为三大类：启发模式、评价模式与一键修改。";
        SentenceSmoothnessGetter sentenceSmoothnessGetter = new SentenceSmoothnessGetter();
        float smoothness = sentenceSmoothnessGetter.getSentenceSmoothness(text);
        System.out.println(smoothness);
    }
}

// 测试线程
class Thread2 extends Thread {
    @Override
    public void run() {
        System.out.println(SingletonAipNlp.getInstance().hashCode());
    }
}