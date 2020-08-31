package org.codedream.epaper.component.datamanager;

import com.baidu.aip.nlp.AipNlp;
import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.configure.SingletonAipNlp;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 提供句子通顺度获取方法
 */
@Slf4j
@Component
public class SentenceSmoothnessGetter {

    /**
     * 通过调用百度接口获取句子通顺度
     *
     * @param text 待处理的文本
     * @return 句子通顺度的值
     */
    public float getSentenceSmoothness(String text) {
        AipNlp client = SingletonAipNlp.getInstance();
        HashMap<String, Object> options = new HashMap<>();
        JSONObject jsonObject = client.dnnlmCn(text, options);

        try {
            Thread.sleep(500);
            float dnn = Float.parseFloat(jsonObject.get("ppl").toString());
            return dnn;
        } catch (Exception e) {
            log.error(e.toString() + "In dnn: " + jsonObject.toString());
            return (float) 0;
        }
    }
}
