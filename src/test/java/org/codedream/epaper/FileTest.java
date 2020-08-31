package org.codedream.epaper;

import org.codedream.epaper.service.IFileService;
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
public class FileTest {
    @Resource
    private IFileService fileService;

    @Test
    public void addFile() throws IOException {
        Path path = Paths.get("文件管理子系统测试.docx");
        InputStream stream = Files.newInputStream(path);
        fileService.saveFile("文件管理子系统测试","docx", stream);
        stream.close();
    }
}
