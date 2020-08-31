package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("句原文列表结构")
@NoArgsConstructor
public class JsonableSTN {

    // 句子ID号
    private Integer stnId;

    // 句子文本内容
    private String text;
}
