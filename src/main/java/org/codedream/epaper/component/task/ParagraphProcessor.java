package org.codedream.epaper.component.task;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.datamanager.ParagraphDivider;
import org.codedream.epaper.component.datamanager.SHA512Encoder;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.repository.article.ArticleRepository;
import org.codedream.epaper.repository.article.ParagraphRepository;
import org.codedream.epaper.repository.article.SentenceRepository;
import org.codedream.epaper.repository.task.TaskRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 提供对段落的预处理方法
 */
@Slf4j
@Component
public class ParagraphProcessor {

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private ParagraphRepository paragraphRepository;

    @Resource
    private ParagraphDivider paragraphDivider;

    @Resource
    private SentenceRepository sentenceRepository;

    @Resource
    private ArticleRepository articleRepository;

    @Resource
    private SHA512Encoder encoder;

    /**
     * 对段落进行分句处理并进行SHA512编码，以便缓存识别
     *
     * @param taskId 任务id，用于获取待处理的段落
     * @see ParagraphDivider
     * @see SHA512Encoder
     */
    public void parse(Integer taskId) {
        // 查找子任务
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) throw new InnerDataTransmissionException(taskId.toString());

        Article article = taskOptional.get().getArticle();
        if (article == null) throw new InnerDataTransmissionException(taskId.toString());

        int stnNum = 0;

        // 段分句及段结构更新
        for (Paragraph paragraph : article.getParagraphs()) {
            // 跳过预处理过的段落
            if (paragraph.isPreprocess()) continue;

            // 段分句处理
            List<String> sentenceTexts = paragraphDivider.divideParagraph(paragraph.getText());
            List<Sentence> sentences = new ArrayList<>();
            for (String text : sentenceTexts) {
                Sentence sentence;
                String hash = encoder.encode(text);

                // 查找句数据库缓存
                Optional<Sentence> sentenceOptional = sentenceRepository.findBySha512Hash(hash);
                if (!sentenceOptional.isPresent()) {
                    // 创建新的句
                    sentence = new Sentence();
                    sentence.setText(text);
                    sentence.setSha512Hash(hash);
                    sentence = sentenceRepository.save(sentence);
                } else {
                    sentence = sentenceOptional.get();
                }
                sentences.add(sentence);
            }

            stnNum += sentences.size();

            // 设置句集合
            Set<Sentence> sentenceSet = new HashSet<>(sentences);
            paragraph.setSentences(sentenceSet);

            // 设置句列表
            for (Sentence sentence : sentences) {
                paragraph.getSentenceList().add(sentence.getId());
            }

            // 设置预处理状态
            paragraph.setPreprocess(true);

            // 更新段落信息
            paragraphRepository.save(paragraph);
        }

        // 更新文章总句数
        taskOptional.get().getArticle().setSentencesNumber(stnNum);
        taskOptional.get().setProgressRate(taskOptional.get().getProgressRate() + 1);
        log.info(String.format("Paragraph preprocess finished, task progress for now is: %d",
                taskOptional.get().getProgressRate()));
        articleRepository.save(taskOptional.get().getArticle());
        taskRepository.save(taskOptional.get());

    }

}
