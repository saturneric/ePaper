package org.codedream.epaper.component.task;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.json.model.JsonableSTNError;
import org.codedream.epaper.component.json.model.JsonableSTNResult;
import org.codedream.epaper.component.json.model.JsonableTaskResult;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.task.CorrectionResult;
import org.codedream.epaper.model.task.SentenceResult;
import org.codedream.epaper.model.task.TaskResult;
import org.codedream.epaper.repository.task.TaskResultRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 提供用于返回前端的结果构造方法
 *
 * 原生的结果数据比较混杂，不适合前端后端JSON格式的数据通信，设立此类以整合结果
 */
@Slf4j
@Component
public class JsonableTaskResultGenerator {

    @Resource
    TaskResultRepository taskResultRepository;

    /**
     * 根据任务结果的原生数据构造用于返回前端的任务结果
     * @param taskId 任务id
     * @return 一个JsonableTaskResult对象，包含构建好的任务结果
     * @see JsonableTaskResult
     */
    public JsonableTaskResult getJsonableTaskResult(Integer taskId) {

        log.info(String.format("Generating jsonable result of task %d", taskId));
        Optional<TaskResult> OTaskResult = taskResultRepository.findByTaskId(taskId);
        double dnnScore = 0;
        double correctScore = 0;
        double emotionScore = 0;
        double e = Math.E;
        int dnnCnt = 0;
        int wrongCnt = 0;
        int oralCnt = 0;
        if (OTaskResult.isPresent()) {
            TaskResult taskResult = OTaskResult.get();
            JsonableTaskResult jsonableTaskResult = new JsonableTaskResult();
            List<JsonableSTNResult> jsonableSTNResults = new ArrayList<>();
            jsonableTaskResult.setTaskId(taskResult.getTaskId());
            taskResult.setSuccess(true);
            taskResult = taskResultRepository.save(taskResult);
            jsonableTaskResult.setSuccess(true);
            Map<Integer, SentenceResult> sentenceResultMap = taskResult.getSentenceResultMap();
            for (SentenceResult result : taskResult.getSentenceResultMap().values()) {
                JsonableSTNResult jsonableSTNResult = new JsonableSTNResult();
                jsonableSTNResult.setStnId(result.getSentenceId());
                jsonableSTNResult.setAppear(0);
                SentenceResult sentenceResult = sentenceResultMap.get(result.getSentenceId());

                // 中立值设置
                jsonableSTNResult.setNeutral(sentenceResult.isNeutral());

                List<JsonableSTNError> jsonableSTNErrorList = new ArrayList<>();

                List<CorrectionResult> correctionResultList = result.getCorrectionResults();
                correctScore += correctionResultList.size();

                log.info("Generating correction result……");
                log.info(String.format("Correction result of sentence %d: %s", sentenceResult.getSentenceId(),
                        correctionResultList.toString()));
                // 寻找该sentence被修改了的位置
                for (CorrectionResult correctionResult : correctionResultList) {
                    JsonableSTNError jsonableSTNError = new JsonableSTNError();
                    jsonableSTNError.setWordIdx(correctionResult.getStartPos());
                    jsonableSTNError.setWordLen(correctionResult.getLength());
                    jsonableSTNError.setType(1);
                    wrongCnt++;
                    if (correctionResult.getCorrectionText().isEmpty()) {
                        jsonableSTNError.setContent("文本存在错误，建议删除");
                    } else {
                        jsonableSTNError.setContent(String.format("文本存在错误，建议修改为：%s",
                                correctionResult.getCorrectionText()));
                    }
                    jsonableSTNErrorList.add(jsonableSTNError);
                }

                // 判断句子情感倾向
                String content;
                JsonableSTNError stnError = new JsonableSTNError();
                if (sentenceResult.isNegative() || sentenceResult.isPositive()) {
                    stnError.setType(2);
                    stnError.setWordIdx(0);
                    stnError.setWordLen(0x7fffffff);
                    if (sentenceResult.isNegative()) {
                        taskResult.getNegativeEmotionsCount().incrementAndGet();
                        float possibility = sentenceResult.getPossibilities().get(0);
                        emotionScore += Math.pow(e, possibility);
                        if (possibility < 0.9) {
                            content = "文本语言较强烈口语化特征，建议修改为书面语。";
                        } else {
                            content = "文本语言极为强烈的口语化特征，建议修改为书面语。";
                        }
                    } else {
                        taskResult.getPositiveEmotionsCount().incrementAndGet();
                        float possibility = sentenceResult.getPossibilities().get(1);
                        emotionScore += Math.pow(2 * e, possibility);
                        if (possibility < 0.99) {
                            content = "文本语言较强烈的口语化特征，建议修改为书面语。";
                        } else {
                            content = "文本语言有极为强烈的口语化特征，建议修改为书面语。";
                        }
                    }
                    oralCnt++;
                    stnError.setContent(content);
                    jsonableSTNErrorList.add(stnError);
                } else if (sentenceResult.getPossibilities().get(1) < 0.99) {
                    stnError.setType(2);
                    stnError.setWordIdx(0);
                    stnError.setWordLen(0x7fffffff);
                    content = "文本疑似存在口语化问题，请注意审查";
                    stnError.setContent(content);
                    jsonableSTNErrorList.add(stnError);
                    oralCnt++;
                }

                if (sentenceResult.getDnn() > 4000) {
                    dnnScore += sentenceResult.getDnn();
                    taskResult.getBrokenSentencesCount().incrementAndGet();
                    JsonableSTNError jsonableSTNError = new JsonableSTNError();
                    jsonableSTNError.setType(3);
                    jsonableSTNError.setContent("句子通顺度存在问题，建议修改");
                    jsonableSTNError.setWordIdx(0);
                    jsonableSTNError.setWordLen(0x7fffffff);
                    jsonableSTNErrorList.add(jsonableSTNError);
                    dnnCnt++;
                } else if (sentenceResult.getDnn() > 2000) {
                    dnnScore += sentenceResult.getDnn();
                    dnnCnt++;
                }
                jsonableSTNResult.setErrorList(jsonableSTNErrorList);
                jsonableSTNResults.add(jsonableSTNResult);
            }
            taskResultRepository.save(taskResult);

            jsonableTaskResult.setWrongTextCount(wrongCnt);
            jsonableTaskResult.setBrokenSentencesCount(dnnCnt);
            jsonableTaskResult.setOralCount(oralCnt);
            jsonableTaskResult.setStnResults(jsonableSTNResults);

            wrongCnt += 4;
            correctScore = Math.PI * Math.log(Math.atan(1.1 * wrongCnt - 10) + 2) / Math.log(e) + 10;
            correctScore = correctScore * 6.3489125794718040652210611515526 + 12.305831203372289317158476382609;

            dnnCnt += 1;
            dnnScore = Math.PI * Math.log(-Math.atan(2 * dnnCnt - 20) + 2) / Math.log(e) + 10;
            dnnScore = dnnScore * 5.8066270155791913101469156905197 + 19.001197895698319575318511547571;


            oralCnt += 1;
            emotionScore = Math.PI * Math.log(-Math.atan(0.9 * oralCnt - 12) + 2) / Math.log(e) + 10;
            emotionScore = emotionScore * 6.4684248579558173553422908337857 + 9.969074019579274789063012724827;

            jsonableTaskResult.setDnnScore(dnnScore);
            jsonableTaskResult.setEmotionScore(emotionScore);
            jsonableTaskResult.setCorrectionScore(correctScore);

            dnnScore *= 0.34482758620689655172413793103448;
            correctScore *= 0.17241379310344827586206896551724;
            emotionScore *= 0.48275862068965517241379310344828;
            jsonableTaskResult.setScore(dnnScore + emotionScore + correctScore);
            return jsonableTaskResult;
        } else {
            throw new NotFoundException("This task has no result yet.");
        }
    }
}
