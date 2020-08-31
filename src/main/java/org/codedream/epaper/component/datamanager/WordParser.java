package org.codedream.epaper.component.datamanager;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.codedream.epaper.configure.AppConfigure;
import org.codedream.epaper.exception.innerservererror.HandlingErrorsException;
import org.codedream.epaper.exception.innerservererror.RuntimeIOException;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.repository.article.ParagraphRepository;
import org.codedream.epaper.service.IArticleService;
import org.codedream.epaper.service.IFileService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

/**
 * Word文档解析
 */
@Component
public class WordParser {
    @Resource
    private IFileService fileService;

    @Resource
    private IArticleService articleService;

    @Resource
    private ParagraphRepository paragraphRepository;

    @Resource
    private SHA512Encoder encoder;

    @Resource
    private AppConfigure configure;

    public Article parse(InputStream stream, String type){
        if(type.equals("doc")){
            return parseDoc(stream);
        }
        else{
            return parseDocx(stream);
        }
    }

    public Integer parse(Integer fileId){
        File file = fileService.getFileInfo(fileId);
        if(file.getType().equals("doc") || file.getType().equals("docx")){
            Article article = parse(fileService.getFile(fileId), file.getType());
            article.setFileId(fileId);
            article = articleService.save(article);
            return article.getId();
        }
        else throw new HandlingErrorsException(file.getType());

    }

    public Article parseDoc(InputStream stream) {
        try {
            WordExtractor extractor = new WordExtractor(stream);

            Article article = articleService.createArticle(null);

            for (String text : extractor.getParagraphText()) {
                if (text.length() > configure.getParagraphMinSize()
                        && text.length() < configure.getParagraphMaxSize()) {
                    // 储存段结构
                    saveParagraph(article ,text);
                }
            }

            // 保存章
            return articleService.save(article);

        } catch (IOException e){
            throw new RuntimeIOException("Doc Parse Error");
        }
    }

    public Article parseDocx(InputStream stream){
        try{
            XWPFDocument doc = new XWPFDocument(stream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);

            Article article = articleService.createArticle(null);

            // 遍历段落
            Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
            while (iterator.hasNext()){
                XWPFParagraph para = iterator.next();
                String text = para.getText();
                if(text.length() > configure.getParagraphMinSize()
                        && text.length() < configure.getParagraphMaxSize()){
                    System.out.println("Paragraph: " + text);
                    // 储存段结构
                    saveParagraph(article ,text);
                }
            }

            // 保存章
            return articleService.save(article);

        } catch (IOException e){
            throw new RuntimeIOException("Docx Parse Error");
        }
    }

    // 储存段结构，并考虑缓存情况
    private void saveParagraph(Article article, String text){
        String hash = encoder.encode(text);
        Paragraph paragraph;
        Optional<Paragraph> paragraphOptional = paragraphRepository.findBySha512Hash(hash);
        if(!paragraphOptional.isPresent()){
            paragraph = articleService.createParagraph(text);
            paragraph.setSha512Hash(hash);
            paragraph = articleService.save(paragraph);
        }
        else{
            paragraph = paragraphOptional.get();
        }

        articleService.addParagraph(article, paragraph);
    }

}
