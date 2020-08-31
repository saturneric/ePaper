package org.codedream.epaper.service;

import javafx.util.Pair;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Phrase;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.repository.article.PhraseRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * 文本处理服务接口
 */
public interface IArticleService {

    /**
     * 创建一个新的{@link Article}对象
     *
     * @param title 文章标题
     * @return 一个新建的
     */
    Article createArticle(String title);

    /**
     * 根据文本创建一个新的{@link Paragraph}对象
     *
     * @param text 文本内容
     * @return 一个新的{@link Paragraph}对象
     */
    Paragraph createParagraph(String text);

    /**
     * 把段落加入到一篇文章中，即建立起一个{@link Article}对象和一个{@link Paragraph}对象之间的关联
     *
     * @param article   待加入段落文章
     * @param paragraph 待加入到文章的段落
     */
    void addParagraph(Article article, Paragraph paragraph);

    /**
     * 查询基本词是否存在
     *
     * @param text 词文本
     * @return 一个布尔值表示是否存在
     * @see PhraseRepository#existsByText(String)
     */
    boolean checkPhraseExist(String text);

    /**
     * 保存基本词，并返回是否存在于数据库中
     *
     * @param text 基本词文本
     * @return 一个Pair<Boolean, Phrase>类型的对象，键值表示是否存在于数据库，值表示基本词构建的Phrase对象</>
     */
    Pair<Boolean, Phrase> savePhrase(String text);

    /**
     * 根据文本持久化一个{@link Sentence}对象，暂未启用
     *
     * @param text 句子文本
     * @return 空
     */
    Sentence saveSentence(String text);

    /**
     * 根据文本持久化一个{@link Paragraph}对象，暂未启用
     *
     * @param text 段落文本
     * @return 空
     */
    Paragraph saveParagraph(String text);

    /**
     * 根据句子的哈希值判断是否存储了相应句子
     *
     * @param hash 句子hash值
     * @return 一个封装在Optional中的句子对象
     */
    Optional<Sentence> findSentenceByHash(String hash);

    /**
     * 根据文本查询基本词是否已经持久化
     *
     * @param text 文本
     * @return Optional封装的Phrase对象
     */
    Optional<Phrase> findPhrase(String text);

    /**
     * 持久化一个{@link Phrase}对象
     *
     * @param phrase 要存储的{@link Phrase}对象
     * @return 持久化的Phrase对象
     */
    Phrase save(Phrase phrase);

    /**
     * 持久化一个{@link Sentence}对象
     *
     * @param sentence 要存储的{@link Phrase}对象
     * @return 持久化的Sentence对象
     */
    Sentence save(Sentence sentence);

    /**
     * 持久化一个{@link Article}对象
     *
     * @param article 要存储的{@link Article}对象
     * @return 持久化的Article对象
     */
    Article save(Article article);

    /**
     * 持久化一个{@link Paragraph}对象
     *
     * @param paragraph 要存储的{@link Paragraph}对象
     * @return 持久化的Paragraph对象
     */
    Paragraph save(Paragraph paragraph);

    /**
     * 通过id寻找一个{@link Phrase}对象
     *
     * @param id 一个{@link Phrase}对线的id
     * @return 封装在Optional内部的Phrase对象
     */
    Optional<Phrase> findPhraseById(int id);

    /**
     * 通过id寻找一个{@link Paragraph}对象
     *
     * @param id 一个{@link Paragraph}对线的id
     * @return 封装在Optional内部的Paragraph对象
     */
    Optional<Paragraph> findParagraphById(int id);

    /**
     * 通过id寻找一个{@link Sentence}对象
     *
     * @param id 一个{@link Sentence}对线的id
     * @return 封装在Optional内部的Sentence对象
     */
    Optional<Sentence> findSentenceById(int id);

    /**
     * 通过id寻找一个{@link Article}对象
     *
     * @param id 一个{@link Article}对线的id
     * @return 封装在Optional内部的Article对象
     */
    Optional<Article> findArticleById(int id);
}
