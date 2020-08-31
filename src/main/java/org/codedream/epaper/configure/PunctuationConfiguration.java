package org.codedream.epaper.configure;

import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
/**
 * 句末标点、引用标点等配置
 */
@Configuration
public class PunctuationConfiguration {

    private static final List<String> expPunctuations = new ArrayList<String>() {{
        add("(");
        add(")");
        add("（");
        add("）");
    }};

    private static final List<String> endOfSentencePunctuations = new ArrayList<String>() {{
        add("。");
        add("？");
        add("！");
    }};

    private static final List<String> warningQuotes = new ArrayList<String>() {{
        add("\"");
        add("\'");
    }};

    private static final List<String> frontPunctuations = new ArrayList<String>() {{
        add("“");
        add("《");
    }};

    private static final List<String> backPunctuations = new ArrayList<String>() {{
        add("”");
        add("》");
        add(")");
        add("）");
    }};

    private static final List<String> quotesPunctuations = new ArrayList<String>() {{
        add("“");
        add("《");
        add("”");
        add("》");
    }};


    static public List<String> getExpPunctuations() {
        return expPunctuations;
    }

    public static List<String> getEndOfSentencePunctuations() {
        return endOfSentencePunctuations;
    }

    public static List<String> getWarningQuotes() {
        return warningQuotes;
    }

    public static List<String> getFrontPunctuations() {
        return frontPunctuations;
    }

    public static List<String> getBackPunctuations() {
        return backPunctuations;
    }

    public static List<String> getQuotesPunctuations() {
        return quotesPunctuations;
    }

}
