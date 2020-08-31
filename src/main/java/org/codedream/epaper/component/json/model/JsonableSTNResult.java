package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("句处理结果")
@NoArgsConstructor
public class JsonableSTNResult {
    // 句ID号
    Integer stnId;

    // 错误类型
    Integer appear;

    // 分数
    Float score;

    // 是否为书面文本
    boolean isNeutral;

    // 错误列表
    List<JsonableSTNError> errorList = new ArrayList<>();
}
