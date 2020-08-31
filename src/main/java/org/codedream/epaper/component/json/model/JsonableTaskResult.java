package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("任务处理结果")
@NoArgsConstructor
public class JsonableTaskResult {

    // 任务ID号
    private Integer taskId;

    // 任务是否成功
    private boolean success;

    // 文章错误位置数量
    private Integer wrongTextCount = 0;

    // 不通顺的句子数量
    private Integer brokenSentencesCount = 0;

    // 口语化的句子数量
    private Integer oralCount = 0;

    // 文章得分
    private Double score = (double) 0;

    // 通顺度得分
    private Double dnnScore = (double) 0;

    // 感情倾向得分
    private Double emotionScore = (double) 0;

    // 文本纠错得分
    private Double correctionScore = (double) 0;

    private List<JsonableSTNResult> stnResults = new ArrayList<>();
}
