package org.codedream.epaper.component.article;

import org.codedream.epaper.component.json.model.JsonableSTN;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得章结构的句列表
 */
@Component
public class GetSentenceFromArticle {

    /**
     * 获得章结构的句列表
     * @param article 章结构
     * @return 句结构列表
     */
    public List<Sentence> get(Article article){
        List<Sentence> sentences = new ArrayList<>();
        for(Paragraph paragraph : article.getParagraphs()){
            sentences.addAll(paragraph.getSentences());
        }
        return sentences;
    }
}
