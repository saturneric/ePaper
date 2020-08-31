package org.codedream.epaper.configure;

import org.springframework.context.annotation.Configuration;

/**
 * 百度API接口所需信息
 */
@Configuration
public class NLPConfigure {

    private static final String APP_ID = "18006539";
    private static final String APP_KEY = "5sdgAnjElUhfuzH1eHFDnWpz";
    private static final String SECRET_KEY = "7DYgD3j0KEO3h0Lxrwq16QUWWShvsvKV";

    public static String getAPPId() {
        return APP_ID;
    }

    public static String getAppKey() {
        return APP_KEY;
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }
}
