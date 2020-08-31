package org.codedream.epaper;

import com.baidu.aip.nlp.AipNlp;
import org.codedream.epaper.component.EPSpringUtil;
import org.codedream.epaper.component.datamanager.ParagraphDivider;
import org.codedream.epaper.configure.NLPConfigure;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Phrase;
import org.codedream.epaper.service.ArticleService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class DividerTest {

    @Resource
    ArticleService articleService;

    @Resource
    EPSpringUtil epSpringUtil;

    @Test
    public void divideSentence() throws JSONException {

        String a = "也只好，通过它了解真相";
        AipNlp client = new AipNlp(NLPConfigure.getAPPId(), NLPConfigure.getAppKey(), NLPConfigure.getSecretKey());
        HashMap<String, Object> hashMap = new HashMap<>();
        JSONObject res = client.lexer(a, hashMap);
        HashMap<String, Object> hashMap1 = new HashMap<>();
        JSONArray items = (JSONArray) res.get("items");
        List<Phrase> phrases = new ArrayList<>();
        for (int i = 0; i < items.length() - 1; i++) {
            JSONObject item = (JSONObject) items.get(i);
            Phrase phrase = new Phrase();
            List<Phrase> basic = new ArrayList<>();
            JSONArray basicArray = (JSONArray) item.get("basic_words");
            System.out.println(basicArray);
            for (int j = 0; j < basicArray.length(); j++) {

                Phrase phrase1 = new Phrase();
                phrase1.setText(basicArray.get(j).toString());
                phrase1 = articleService.save(phrase1);
                basic.add(phrase1);
            }
            phrase.setText(item.get("item").toString());
            phrase.getBasicPhrase().addAll(basic);
            phrase.setPos(item.get("pos").toString());
        }
        System.out.println(items);
    }

    @Test
    public void divideParagraph() throws FileNotFoundException {

//        String path = "D:\\Contests\\SIC\\log.txt";
//        FileOutputStream fileOutputStream = new FileOutputStream(path);
//        String text = "1111aaaaaaaaaaaaaaaaaaaaaa我。Smart Guiding of Academic Paper Writing。" +
//                "Version 0.4.2.191231_alpha。" +
//                "All Rights Reserved。2019-12-1 ~ 2019-12-15\n" +
//                "2019-12-16 ~ 2019-12-31。";
//        ParagraphDivider paragraphDivider = epSpringUtil.getBean(ParagraphDivider.class);
//        Paragraph paragraph = paragraphDivider.divideParagraph(text);
//        BufferedWriter out = null;
        /**
         try {
         out = new BufferedWriter(new OutputStreamWriter(
         new FileOutputStream(path, true)));
         for (int i = 0; i < paragraph.getSentences().size(); i++) {
         out.write(paragraph.getSentences().get(i).getText());
         out.write("\n");
         }
         } catch (Exception e) {
         e.printStackTrace();
         } finally {
         try {
         out.close();
         } catch (IOException e) {
         e.printStackTrace();
         }
         }
         */

    }

    @Test
    public void littleTest() throws JSONException {

        String text = "alpha_asdfadfafsadfafs";
        AipNlp client = new AipNlp(NLPConfigure.getAPPId(), NLPConfigure.getAppKey(), NLPConfigure.getSecretKey());
        HashMap<String, Object> hashMap = new HashMap<>();
        System.out.println(client.lexer(text, hashMap));
        /*JSONObject res = client.wordEmbedding(text, hashMap);
        System.out.println(res.toString());
        JSONArray vec = res.getJSONArray("vec");
        List<Float> floatList = new ArrayList<>();
        for (int i = 0; i < vec.length(); i++) {

            String str = vec.getString(i);
            float vecUnit = Float.parseFloat(str);
            floatList.add(vecUnit);
        }

        System.out.println(floatList);*/
    }
}
