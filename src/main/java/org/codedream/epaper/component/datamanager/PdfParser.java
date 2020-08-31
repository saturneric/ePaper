package org.codedream.epaper.component.datamanager;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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
import java.util.Optional;

@Component
public class PdfParser {

    @Resource
    private IFileService fileService;

    @Resource
    private IArticleService articleService;

    @Resource
    private ParagraphRepository paragraphRepository;

    @Resource
    private SHA512Encoder encoder;

    public Article parse(InputStream stream){

        try {

            Article article = articleService.createArticle(null);

            PDDocument doc = PDDocument.load(stream);

            PDFTextStripper textStripper = new PDFTextStripper();

            String content = textStripper.getText(doc);

            String regA = "^[\\u4e00-\\u9fa5a-zA-Z0-9。，？!；、：（）“”]";
            String regB = "[\\s|.,/?\"%$#@*^~`()+=\\-{}<>\\///_]";
            content = content.replaceAll(regA, "");
            content = content.replaceAll(regB, "");


            saveParagraph(article, content);

            doc.close();

            return article;

        } catch (IOException e) {
            throw new RuntimeIOException(e.getMessage());
        }
    }

    public Integer parse(Integer fileId){
        File file = fileService.getFileInfo(fileId);
        if(file.getType().equals("pdf")){
            Article article = parse(fileService.getFile(fileId));

            article.setFileId(fileId);
            article = articleService.save(article);
            return article.getId();
        }
        else throw new HandlingErrorsException(file.getType());

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
