package org.codedream.epaper;

import org.codedream.epaper.component.datamanager.ReportGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ReportTest {

    @Resource
    private ReportGenerator reportGenerator;

    @Test
    public void simpleGenerate() throws IOException {
        reportGenerator.saveByFile("Report.pdf", reportGenerator.generate(4009));
    }
}
