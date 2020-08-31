package org.codedream.epaper.configure;

import org.springframework.stereotype.Component;

/**
 * 应用程序常用配置信息
 * 用于常见的应用程序本身的相关信息的引用
 */
@Component
public class AppConfigure {
    /**
     * 获得应用程序的中文名
     * @return 返回包含完整内容的字符串
     */
    public String getName() {
        return "智慧学术论文行文指导服务端";
    }

    /**
     * 获得应用程序的版本号
     * @return 返回版本号内容的字符串
     */
    public String getVersion() {
        return "0.0.1_200204";
    }

    /**
     * 获得应用程序的英文名
     * @return 返回包含完整内容的字符串
     */
    public String getEnglishName() {
        return "ePaper";
    }

    /**
     * 获得开发小组的名称
     * @return 包含完整内容的字符串
     */
    public String getOrganization() {
        return "码梦工坊";
    }

    /**
     * 文件服务储存路径
     * @return 字符串
     */
    public String getFilePath(){
        return "./FILES/";
    }

    /**
     * 上传的文件的大小限制（字节）
     * 预设值：16MB
     * @return 数值
     */
    public Integer getFileMaxSize(){
        return 16000000;
    }

    /**
     * 单段的最小字数阈值
     * @return 数值
     */
    public Integer getParagraphMinSize(){
        return 16;
    }

    /**
     * 单段的最大字数阈值
     * @return 数值
     */
    public Integer getParagraphMaxSize(){
        return 2048;
    }

    /**
     * 批处理任务最大句数目阈值
     * @return 数值
     */
    public Long getBPTMaxSentenceNumber() {
        return 32768L;
    }

    /**
     * 批处理任务最少句数目阈值
     * @return 数值
     */
    public Long getBPTMinSentenceNumber(){
        return 1024L;
    }

    /**
     * 子服务器失联等待时间
     * @return 数值
     */
    public Integer gerChildServerRegisterTimeout(){
        return 300;
    }

    /**
     * 子任务等待被加入批处理任务最长时间
     * @return 数值
     */
    public Integer gerMaxTaskDelayTime(){
        return 30;
    }

    /**
     * 批处理任务处理最长时间
     * @return 数值
     */
    public Integer gerMaxBPTProcessDelayTime(){
        return 300;
    }


    /**
     * 单页句数
     * @return 数值
     */
    public Integer getSentencePrePage(){
        return 10;
    }

}
