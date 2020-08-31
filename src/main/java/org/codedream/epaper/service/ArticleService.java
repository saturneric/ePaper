package org.codedream.epaper.service;

import javafx.util.Pair;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.article.Phrase;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.repository.article.ArticleRepository;
import org.codedream.epaper.repository.article.ParagraphRepository;
import org.codedream.epaper.repository.article.PhraseRepository;
import org.codedream.epaper.repository.article.SentenceRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 文章服务层，提供包括文章解析、创建、存储等方法在内的有关文章处理的服务。
 * <p>
 * 除了{@code Article}对象的DAO相关方法外，也提供更细粒度对象的DAO相关方法也基于此服务，
 * 如 {@code Phrase}、{@code Paragraph}以及{@code Sentence}
 * <p>
 * 所有涉及的持久化操作都会进行缓存校验
 */
@Service
public class ArticleService implements IArticleService {

    @Resource
    ArticleRepository articleRepository;

    @Resource
    PhraseRepository phraseRepository;

    @Resource
    SentenceRepository sentenceRepository;

    @Resource
    ParagraphRepository paragraphRepository;

    /**
     * 创建一个新的{@link Article}对象
     *
     * @param title 文章标题
     * @return 一个新建的
     */
    @Override
    public Article createArticle(String title) {
        return new Article();
    }

    /**
     * 根据文本创建一个新的{@link Paragraph}对象
     *
     * @param text 文本内容
     * @return 一个新的{@link Paragraph}对象
     */
    @Override
    public Paragraph createParagraph(String text) {
        Paragraph paragraph = new Paragraph();
        paragraph.setText(text);
        return paragraph;
    }

    /**
     * 把段落加入到一篇文章中，即建立起一个{@link Article}对象和一个{@link Paragraph}对象之间的关联
     *
     * @param article   待加入段落文章
     * @param paragraph 待加入到文章的段落
     */
    @Override
    public void addParagraph(Article article, Paragraph paragraph) {
        article.getParagraphs().add(paragraph);
        article.setTotalLength(article.getTotalLength() + paragraph.getText().length());
    }

    /**
     * 查询基本词是否存在
     *
     * @param text 词文本
     * @return 一个布尔值表示是否存在
     * @see PhraseRepository#existsByText(String)
     */
    @Override
    public boolean checkPhraseExist(String text) {
        return phraseRepository.existsByText(text);
    }

    /**
     * 根据句子的哈希值判断是否存储了相应句子
     *
     * @param hash 句子hash值
     * @return 一个封装在Optional中的句子对象
     */
    @Override
    public Optional<Sentence> findSentenceByHash(String hash) {
        return sentenceRepository.findBySha512Hash(hash);
    }

    /**
     * 保存基本词，并返回是否存在于数据库中
     *
     * @param text 基本词文本
     * @return 一个Pair<Boolean, Phrase>类型的对象，键值表示是否存在于数据库，值表示基本词构建的Phrase对象</>
     */
    @Override
    public Pair<Boolean, Phrase> savePhrase(String text) {
        Phrase phrase;
        Optional<Phrase> phraseOptional = phraseRepository.findByText(text);
        if (!phraseOptional.isPresent()) {
            phrase = new Phrase();
            phrase.setText(text);
            return new Pair<>(false, phraseRepository.save(phrase));
        } else {
            return new Pair<>(true, phraseOptional.get());
        }
    }

    /**
     * 根据文本持久化一个{@link Sentence}对象，暂未启用
     *
     * @param text 句子文本
     * @return 空
     */
    @Override
    public Sentence saveSentence(String text) {
        return null;
    }

    /**
     * 根据文本持久化一个{@link Paragraph}对象，暂未启用
     *
     * @param text 段落文本
     * @return 空
     */
    @Override
    public Paragraph saveParagraph(String text) {
        return null;
    }

    /**
     * 根据文本查询基本词是否已经持久化
     *
     * @param text 文本
     * @return Optional封装的Phrase对象
     */
    @Override
    public Optional<Phrase> findPhrase(String text) {
        return phraseRepository.findByText(text);
    }


    /**
     * 持久化一个{@link Phrase}对象
     *
     * @param phrase 要存储的{@link Phrase}对象
     * @return 持久化的Phrase对象
     */
    @Override
    public Phrase save(Phrase phrase) {
        return phraseRepository.save(phrase);
    }

    /**
     * 持久化一个{@link Sentence}对象
     *
     * @param sentence 要存储的{@link Phrase}对象
     * @return 持久化的Sentence对象
     */
    @Override
    public Sentence save(Sentence sentence) {
        return sentenceRepository.save(sentence);
    }

    /**
     * 持久化一个{@link Article}对象
     *
     * @param article 要存储的{@link Article}对象
     * @return 持久化的Article对象
     */
    @Override
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    /**
     * 持久化一个{@link Paragraph}对象
     *
     * @param paragraph 要存储的{@link Paragraph}对象
     * @return 持久化的Paragraph对象
     */
    @Override
    public Paragraph save(Paragraph paragraph) {
        return paragraphRepository.save(paragraph);
    }

    /**
     * 通过id寻找一个{@link Phrase}对象
     *
     * @param id 一个{@link Phrase}对线的id
     * @return 封装在Optional内部的Phrase对象
     */
    @Override
    public Optional<Phrase> findPhraseById(int id) {
        return phraseRepository.findById(id);
    }

    /**
     * 通过id寻找一个{@link Paragraph}对象
     *
     * @param id 一个{@link Paragraph}对线的id
     * @return 封装在Optional内部的Paragraph对象
     */
    @Override
    public Optional<Paragraph> findParagraphById(int id) {
        return paragraphRepository.findById(id);
    }

    /**
     * 通过id寻找一个{@link Sentence}对象
     *
     * @param id 一个{@link Sentence}对线的id
     * @return 封装在Optional内部的Sentence对象
     */
    @Override
    public Optional<Sentence> findSentenceById(int id) {
        return sentenceRepository.findById(id);
    }

    /**
     * 通过id寻找一个{@link Article}对象
     *
     * @param id 一个{@link Article}对线的id
     * @return 封装在Optional内部的Article对象
     */
    @Override
    public Optional<Article> findArticleById(int id) {
        return articleRepository.findById(id);
    }
}
