package org.codedream.epaper.component.json.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel("句分页结构")
@NoArgsConstructor
public class JsonableSTNPage {
    // 页序号
    Integer page;

    // 页数
    Integer all;

    // 句列表
    List<JsonableSTN> stns;
}
