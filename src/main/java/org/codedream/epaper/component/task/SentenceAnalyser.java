package org.codedream.epaper.component.task;

import org.codedream.epaper.component.datamanager.SentenceSmoothnessGetter;
import org.codedream.epaper.component.datamanager.TextCorrector;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.CorrectionResult;
import org.codedream.epaper.model.task.SentenceResult;
import org.codedream.epaper.repository.task.SentenceResultRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 句分析器，提供句子分析相关方法以及持久化方法
 *
 * @see TextCorrector
 * @see SentenceSmoothnessGetter
 */
@Component
public class SentenceAnalyser {

    @Resource
    private TextCorrector textCorrector;

    @Resource
    private SentenceSmoothnessGetter dnnGetter;

    @Resource
    private SentenceResultRepository sentenceResultRepository;

    public SentenceResult analyse(Sentence sentence) {
        // 创建与预填写句子与处理结果结构
        SentenceResult sentenceResult = new SentenceResult();
        sentenceResult.setSentenceId(sentence.getId());
        sentenceResult.setDnn(dnnParse(sentence));
        correct(sentenceResult, sentence);
        // 储存句分析结果
        return sentenceResultRepository.save(sentenceResult);
    }

    /**
     * 获取句子的文本纠错结果
     *
     * @param sentence 句子
     */
    private void correct(SentenceResult sentenceResult, Sentence sentence) {
        List<CorrectionResult> correctionResultList = textCorrector.correctText(sentence.getText());
        sentenceResult.setCorrectionResults(correctionResultList);
    }

    /**
     * 获取DNN处理结果
     *
     * @param sentence 待处理句子
     * @return DNN处理结果
     * @see SentenceSmoothnessGetter#getSentenceSmoothness(String)
     */
    private synchronized float dnnParse(Sentence sentence) {
        return dnnGetter.getSentenceSmoothness(sentence.getText());
    }
}
