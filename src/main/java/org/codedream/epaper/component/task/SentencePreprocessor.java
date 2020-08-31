package org.codedream.epaper.component.task;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.article.GetSentenceFromArticle;
import org.codedream.epaper.component.datamanager.SentenceDivider;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Phrase;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.repository.article.SentenceRepository;
import org.codedream.epaper.repository.task.TaskRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 用于提供句子预处理相关方法，以及对应的持久化方法
 */
@Slf4j
@Component
public class SentencePreprocessor {

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private SentenceDivider sentenceDivider;

    @Resource
    private SentenceRepository sentenceRepository;

    @Resource
    private GetSentenceFromArticle getSentenceFromArticle;


    /**
     * 取出任务中的句子并进行预处理
     *
     * @param taskId 任务id
     * @see SentenceDivider#divideSentence(String)
     * @see GetSentenceFromArticle#get(Article)
     */
    public void parse(Integer taskId) {
        // 查找子任务
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) throw new InnerDataTransmissionException(taskId.toString());

        Task task = taskOptional.get();
        Article article = task.getArticle();
        if (article == null) throw new InnerDataTransmissionException(taskId.toString());

        List<Sentence> sentences = getSentenceFromArticle.get(article);

        for (Sentence sentence : sentences) {
            // 跳过缓存句
            if (sentence.isPreprocess()) continue;

            List<Phrase> phrases = sentenceDivider.divideSentence(sentence.getText());

            sentence.getPhrases().addAll(phrases);
            sentence.setPreprocess(true);

            for (Phrase phrase : phrases) {
                sentence.getPhraseList().add(phrase.getId());
            }

            sentenceRepository.save(sentence);
        }
        task.setProgressRate(task.getProgressRate() + 1);
        log.info(String.format("Sentence preprocess finished, task progress for now is: %d", task.getProgressRate()));
        taskRepository.save(task);
    }


}
