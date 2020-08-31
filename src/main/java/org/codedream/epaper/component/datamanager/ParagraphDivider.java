package org.codedream.epaper.component.datamanager;

import org.codedream.epaper.configure.PunctuationConfiguration;
import org.codedream.epaper.model.article.Paragraph;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将一个段落分为几个句子
 */
@Component
public class ParagraphDivider {

    /**
     * 将一个段落划分为若干句，并进行相应的持久化
     *
     * @param text 需要划分的段落文本
     * @return 段落中的所有被划分好的句子
     */
    public List<String> divideParagraph(String text) {

        if (text.isEmpty()) return null;
        Paragraph paragraph = new Paragraph();
        paragraph.setText(text);
        List<String> back = PunctuationConfiguration.getBackPunctuations();
        String[] arr = text.split("。|！|？|……|\\?|：");
        List<String> sentenceTexts = Arrays.asList(arr);

        List<String> sentences = new ArrayList<>();
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        for (int i = 0; i < sentenceTexts.size(); i++) {
            String sentenceText = sentenceTexts.get(i);
            Matcher m = p.matcher(sentenceText);
            int len = sentenceTexts.get(i).length();
            if (!m.find() || len < 15 || len > 510) continue;
            if (back.contains(sentenceTexts.get(i).charAt(0)) && sentences.size() > 1) {
                sentences.set(i - 1, sentences.get(i - 1) + sentenceTexts.get(i).charAt(0));
            }

            sentences.add(sentenceTexts.get(i));
        }

        return sentences;
    }
}
