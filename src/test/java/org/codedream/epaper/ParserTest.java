package org.codedream.epaper;

import org.codedream.epaper.component.datamanager.PdfParser;
import org.codedream.epaper.component.datamanager.WordParser;
import org.codedream.epaper.model.article.Paragraph;
import org.codedream.epaper.service.IArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ParserTest {
    @Resource
    private WordParser wordParser;

    @Resource
    private PdfParser pdfParser;

    @Resource
    private IArticleService articleService;

    @Test
    public void docxTest() throws IOException {
        Path path = Paths.get("大量文字测试.docx");
        InputStream stream = Files.newInputStream(path);
        wordParser.parse(stream, "docx");
        stream.close();
    }

    @Test
    public void pdfTest() throws IOException {
        Path path = Paths.get("大量文字测试.pdf");
        InputStream stream = Files.newInputStream(path);
        pdfParser.parse(stream);
        stream.close();
    }


    @Test
    public void unitTest() {
        Paragraph paragraph = new Paragraph();
        paragraph.setText("");
        paragraph.setPreprocess(false);
        paragraph.setSentences(null);
        paragraph = articleService.save(paragraph);
    }
}
