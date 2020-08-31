package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.BatchProcessingTask;
import org.codedream.epaper.model.task.Task;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("批处理任务结构")
@NoArgsConstructor
public class JsonableBPT {
    private Integer id;
    private Integer stnNumber;
    private List<JsonableSTN> stns = new ArrayList<>();

    public JsonableBPT(BatchProcessingTask bpt){
        this.id = bpt.getId();
        this.stnNumber = bpt.getSentencesNumber();

        for (Task task : bpt.getTasks()){
            for(Paragraph paragraph : task.getArticle().getParagraphs()){
                for(Sentence sentence : paragraph.getSentences()){
                    // 检查是否已经深处理完毕
                    if(sentence.isDeepProcess()) continue;
                    
                    JsonableSTN stn = new JsonableSTN();
                    stn.setStnId(sentence.getId());
                    stn.setText(sentence.getText());
                    this.stns.add(stn);
                }
            }
        }

    }
}
