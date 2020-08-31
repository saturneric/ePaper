package org.codedream.epaper.component.datamanager;

import com.baidu.aip.nlp.AipNlp;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.configure.SingletonAipNlp;
import org.codedream.epaper.model.article.Phrase;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.service.ArticleService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 提供句子分词方法，并通过{@link ArticleService}进行相应持久化操作
 */
@Slf4j
@Component
public class SentenceDivider {

    @Resource
    ArticleService articleService;

    /**
     * 将句子分词
     *
     * @param text 要进行分词操作的句子
     * @return 一个已经持久化的、分完词的句子
     */
    public List<Phrase> divideSentence(String text) {

        if (text.isEmpty()) return null;
        AipNlp client = SingletonAipNlp.getInstance();
        HashMap<String, Object> hashMap = new HashMap<>();
        List<Phrase> phrases = new ArrayList<>();
        String f = "";
        try {
            JSONObject res = client.lexer(text, hashMap);
            f = "1";
            boolean lexerFlag = true;
            Iterator<String> iterator = res.keys();
            while (iterator.hasNext()) {
                if (iterator.next().equals("error_msg")) {
                    lexerFlag = false;
                    break;
                }
            }

            if (lexerFlag) {
                f = "2";
                JSONArray items = (JSONArray) res.get("items");
                f = "After lexer";
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = (JSONObject) items.get(i);
                    List<Phrase> basic = new ArrayList<>();
                    f = "Before basicArray";
                    JSONArray basicArray = (JSONArray) item.get("basic_words");

                    for (int j = 0; j < basicArray.length(); j++) {

                        f = "In basicArray";
                        Pair<Boolean, Phrase> phrasePair = articleService.savePhrase(basicArray.get(j).toString());

                        basic.add(phrasePair.getValue());
                    }

                    f = "Before vec";
                    JSONObject vecRes = new JSONObject();


                    Pair<Boolean, Phrase> phrasePair = articleService.savePhrase(item.get("item").toString());
                    Phrase phrase = phrasePair.getValue();

                    if(!phrasePair.getKey()){
                        phrase.setText(item.get("item").toString());
                        phrase.getBasicPhrase().addAll(basic);
                        phrase.setPos(item.get("pos").toString());
                        phrase = articleService.save(phrase);
                    }


                    Iterator<String> keys = vecRes.keys();
                    boolean flag = true;
                    while (keys.hasNext()) {
                        if (keys.next().equals("error_msg")) {
                            flag = false;
                            break;
                        }
                    }
                    flag = false;
                    if (flag) {
                        f = "Before vecArray";
                        JSONArray vec = vecRes.getJSONArray("vec");
                        List<Float> floatList = new ArrayList<>();
                        for (int j = 0; j < vec.length(); j++) {
                            f = "In vecArray";
                            String str = vec.getString(i);
                            f = "Before parsing to float";
                            float vecUnit = Float.parseFloat(str);
                            floatList.add(vecUnit);
                        }
                        phrase.setVec(floatList);
                    }

                    phrases.add(phrase);

                }
            }
        } catch (Exception e) {
            log.error(e.toString() + ": f");
        }

        return phrases;
    }



}
