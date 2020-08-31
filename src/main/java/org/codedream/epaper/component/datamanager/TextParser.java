package org.codedream.epaper.component.datamanager;

import javassist.bytecode.ByteArray;
import org.codedream.epaper.exception.innerservererror.RuntimeIOException;
import org.codedream.epaper.model.article.Article;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.repository.article.ParagraphRepository;
import org.codedream.epaper.service.IArticleService;
import org.codedream.epaper.service.IFileService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Component
public class TextParser {
    @Resource
    private IFileService fileService;

    @Resource
    private IArticleService articleService;

    @Resource
    private ParagraphRepository paragraphRepository;

    @Resource
    private SHA512Encoder encoder;

    public Integer parse(Integer fileId) {
        File file = fileService.getFileInfo(fileId);
        if(file.getType().equals("plain")){
            Article article = articleService.createArticle(null);
            InputStream stream = fileService.getFile(fileId);

            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

            try {

                // 数据内存写出
                int readBits = 0;
                byte[] rawBytes = new byte[1024];
                while ((readBits = stream.read(rawBytes)) != -1) {
                    arrayOutputStream.write(rawBytes, 0, readBits);
                }

                saveParagraph(article, new String(arrayOutputStream.toByteArray()));
                article.setFileId(fileId);
                article = articleService.save(article);

                return article.getId();
            }
            catch (IOException e){
                throw new  RuntimeIOException(e.getMessage());
            }

        }

        return null;
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
