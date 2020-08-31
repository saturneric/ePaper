package org.codedream.epaper.component.datamanager;

import lombok.Data;
import org.codedream.epaper.component.json.model.JsonableSTNError;
import org.codedream.epaper.component.json.model.JsonableSTNResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class STNResult {
    private Integer id;
    private String text;
    private List<JsonableSTNError> stnResultList = new ArrayList<>();
}
