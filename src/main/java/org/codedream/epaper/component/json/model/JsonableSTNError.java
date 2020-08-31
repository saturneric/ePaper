package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("句错误结构")
@NoArgsConstructor
public class JsonableSTNError {

    // 序列号（index）
    private Integer wordIdx;

    // 词长
    private Integer wordLen;

    // 错误类型
    private Integer type;

    // 错误内容
    private String content;
}
