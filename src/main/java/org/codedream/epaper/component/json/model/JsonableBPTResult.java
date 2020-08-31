package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("批处理任务结果返回结构")
@NoArgsConstructor
public class JsonableBPTResult {
    // 句子Id
    private Integer stnid;
    // 标签的预测值
    private List<Float> tagPossible = new ArrayList<>();
}
