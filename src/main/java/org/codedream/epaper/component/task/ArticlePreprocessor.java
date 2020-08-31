package org.codedream.epaper.component.task;

import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.datamanager.PdfParser;
import org.codedream.epaper.component.datamanager.TextParser;
import org.codedream.epaper.component.datamanager.WordParser;
import org.codedream.epaper.exception.innerservererror.HandlingErrorsException;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.repository.article.ArticleRepository;
import org.codedream.epaper.repository.task.TaskRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 用于对文章的预处理。
 * 包括对word文档的解析（{@link WordParser}）
 */
@Slf4j
@Component
public class ArticlePreprocessor {

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private ArticleRepository articleRepository;

    @Resource
    private WordParser wordParser;

    @Resource
    private TextParser textParser;

    @Resource
    private PdfParser pdfParser;


    /**
     * 预处理任务中存储的文章并将其持久化
     *
     * @param taskId 任务id
     */
    public void parse(Integer taskId) {

        // 查找子任务
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) throw new InnerDataTransmissionException(taskId.toString());

        Task task = taskOptional.get();
        File file = task.getFile();
        if (file == null) throw new InnerDataTransmissionException();

        task.setFile(file);

        Integer articleId = null;

        // 章分段处理
        switch (file.getType()) {
            case "doc":
            case "docx":
                articleId = wordParser.parse(file.getId());
                break;
            case "plain":
                articleId = textParser.parse(file.getId());
                break;
            case "pdf":
                articleId = pdfParser.parse(file.getId());
                break;
            default:
                throw new HandlingErrorsException(file.getType());
        }

        if(articleId == null) throw new HandlingErrorsException(file.getId().toString());

        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if (!optionalArticle.isPresent()) throw new InnerDataTransmissionException();

        task.setArticle(optionalArticle.get());
        task.setProgressRate(task.getProgressRate() + 1);
        log.info(String.format("Article preprocess finished, task progress for now is: %d", task.getProgressRate()));
        taskRepository.save(task);
    }

}
