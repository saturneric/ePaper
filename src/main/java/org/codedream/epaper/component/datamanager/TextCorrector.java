package org.codedream.epaper.component.datamanager;

import com.baidu.aip.nlp.AipNlp;
import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.configure.SingletonAipNlp;
import org.codedream.epaper.model.task.CorrectionResult;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 用于文本纠错
 */
@Slf4j
@Component
public class TextCorrector {

    /**
     * 文本纠错
     *
     * @param text 需要纠错的文本
     * @return 一个纠正类的列表，保存句子需要修改的信息
     * 值的位置；corr_text，存放纠错后文本；org_text：存放原文本。
     */
    public List<CorrectionResult> correctText(String text) {

        if (text.isEmpty()) return new ArrayList<>();
        AipNlp client = SingletonAipNlp.getInstance();
        HashMap<String, Object> options = new HashMap<>();
        List<CorrectionResult> correctionResults = new ArrayList<>();
        String correction;
        try {
            JSONObject res = (JSONObject) client.ecnet(text, options).get("item");
            if (res.get("vec_fragment") == null) {
                return correctionResults;
            }
            correction = (String) res.get("correct_query");
            DiffMatchPatch dmp = new DiffMatchPatch();
            List<DiffMatchPatch.Diff> diffs = dmp.diff_main(text, correction);
            int p = 0;
            int size = diffs.size();
            for (int i = 0; i < size; i++) {
                DiffMatchPatch.Diff diff = diffs.get(i);
                if (diff.operation.equals(DiffMatchPatch.Operation.EQUAL) ||
                        diff.operation.equals(DiffMatchPatch.Operation.INSERT)) {
                    p += diff.text.length();
                    continue;
                }
                CorrectionResult correctionResult = new CorrectionResult();
                correctionResult.setStartPos(p);
                correctionResult.setLength(diff.text.length());
                if (i == size - 1 || !diffs.get(i + 1).operation.equals(DiffMatchPatch.Operation.INSERT)) {
                    correctionResult.setCorrectionText("");
                } else {
                    DiffMatchPatch.Diff nextDiff = diffs.get(i + 1);
                    correctionResult.setCorrectionText(nextDiff.text);
                }
                correctionResults.add(correctionResult);
            }
        } catch (Exception e) {
            log.error(e.toString() + ": Failed to analyze \"" + text + "\"");
        }

        return correctionResults;
    }
}
