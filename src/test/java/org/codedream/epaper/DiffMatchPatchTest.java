package org.codedream.epaper;

import org.codedream.epaper.component.datamanager.DiffMatchPatch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.LinkedList;

@SpringBootTest
public class DiffMatchPatchTest {

    @Resource
    DiffMatchPatch dmp = new DiffMatchPatch();

    @Test
    public void test() {
        String text1 = "我们预备了缓存数据库功能，将论文种常见的行文错误进行特征编码后缓存。";
        String text2 = "我们预备了缓存数据库功能，将论文中常见的行文错误进行特征编码后缓存。";
        LinkedList<DiffMatchPatch.Diff> diff = dmp.diff_main(text1, text2);
        System.out.println(diff.toString());
    }
}
