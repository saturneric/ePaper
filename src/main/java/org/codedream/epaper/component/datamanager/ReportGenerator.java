package org.codedream.epaper.component.datamanager;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.helpers.Loader;
import org.codedream.epaper.component.article.GetSentenceFromArticle;
import org.codedream.epaper.component.json.model.JsonableSTNResult;
import org.codedream.epaper.component.json.model.JsonableTaskResult;
import org.codedream.epaper.component.task.JsonableTaskResultGenerator;
import org.codedream.epaper.exception.innerservererror.HandlingErrorsException;
import org.codedream.epaper.exception.innerservererror.InnerDataTransmissionException;
import org.codedream.epaper.exception.innerservererror.RuntimeIOException;
import org.codedream.epaper.model.article.Sentence;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.model.task.Task;
import org.codedream.epaper.service.FileService;
import org.codedream.epaper.service.IFileService;
import org.codedream.epaper.service.ITaskService;
import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class ReportGenerator {
    @Resource
    private ITaskService taskService;

    @Resource
    private IFileService fileService;

    @Resource
    private JsonableTaskResultGenerator taskResultGenerator;

    @Resource
    private GetSentenceFromArticle getSentenceFromArticle;

    Mapper fontMapper = null;

    public byte[] generate(Integer taskId){
        Optional<Task> taskOptional =taskService.getTaskInfo(taskId);

        if(!taskOptional.isPresent()) throw new InnerDataTransmissionException(taskId.toString());

        Task task = taskOptional.get();

        // 获得数据列表
        Map<String, Object> dataMap = processDataMap(task);

        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);
        Template template = getTemplate("report.ftl");

        try {

            template.process(dataMap, writer);

            // 转换为Word
            String xmlStr = stringWriter.toString();
            ByteArrayInputStream in = new ByteArrayInputStream(xmlStr.getBytes());
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(in);

            wordMLPackage.save(new java.io.File("./Report.docx"));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


            wordMLPackage.setFontMapper(generateFrontMapper());

            // 转换为PDF
            Docx4J.toPDF(wordMLPackage, outputStream);



            return outputStream.toByteArray();


        } catch (IOException | TemplateException | Docx4JException e){
            e.printStackTrace();
            throw new HandlingErrorsException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized Mapper generateFrontMapper() throws Exception {
        if (this.fontMapper == null) {
//            URL simSumUrl = ResourceUtils.getURL("classpath:fonts/simsun.ttc");
            PhysicalFonts.discoverPhysicalFonts();
            URL simSumUrl = ResourceUtils.getURL("./fonts/simsun.ttc");
            PhysicalFonts.addPhysicalFont("SimSun", simSumUrl);

            this.fontMapper = new IdentityPlusMapper();
            fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
            fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
            fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
            fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
            fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
            fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
            fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
            fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
            fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
            fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
            fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
            fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
            fontMapper.put("等线", PhysicalFonts.get("SimSun"));
            fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
            fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
            fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
            fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
            fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
            fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
            fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
            fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
            fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
            fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
            fontMapper.put("新細明體", PhysicalFonts.get("SimSun"));
            //解决宋体（正文）和宋体（标题）的乱码问题
            PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
            PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));

            return this.fontMapper;
        }
        return this.fontMapper;
    }

    public void saveByFile(String path, byte[] bytes) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(path);
        outputStream.write(bytes);
        outputStream.close();
    }

    public Integer saveByFileService(byte[] byteArray){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        return fileService.saveFile(UUID.randomUUID().toString() + ".pdf", "pdf", inputStream);
    }

    private Map<String, Object> processDataMap(Task task){

        File file = task.getFile();
        if(file == null) throw new InnerDataTransmissionException(task.toString());

        JsonableTaskResult taskResult = taskResultGenerator.getJsonableTaskResult(task.getId());

        Map<String, Object> dataMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        dataMap.put("articleTitle", file.getName());
        dataMap.put("datetime", sdf.format(new Date()));

        if(taskResult.getScore() > 90)
            dataMap.put("grade", "A");
        else if(taskResult.getScore() > 80)
            dataMap.put("grade", "B");
        else if(taskResult.getScore() > 60)
            dataMap.put("grade", "C");
        else
            dataMap.put("grade", "D");

        dataMap.put("wdScore", taskResult.getCorrectionScore());
        dataMap.put("fqScore", taskResult.getDnnScore());
        dataMap.put("flScore", taskResult.getEmotionScore());


        List<Sentence> sentences = getSentenceFromArticle.get(task.getArticle());

        int totalLen = 0;
        for(Sentence sentence : sentences)
            totalLen += sentence.getText().length();

        dataMap.put("svgLen", totalLen / sentences.size());
        dataMap.put("stnNum", sentences.size());
        dataMap.put("userId", task.getUser().getId());
        dataMap.put("errStatus", "正态分布");
        dataMap.put("wEum", taskResult.getWrongTextCount());
        dataMap.put("pcsTime", task.getEndDate().getTime() - task.getCreateDate().getTime());
        dataMap.put("fqRum", taskResult.getBrokenSentencesCount());
        dataMap.put("flEum", taskResult.getOralCount());
        dataMap.put("status", "完成");

        Map<Integer, STNResult> stnResults = new HashMap<>();

        Map<Integer, Sentence> sentenceMap = new HashMap<>();

        for(Sentence sentence : sentences){
            sentenceMap.put(sentence.getId(), sentence);
        }

        for(JsonableSTNResult jsonableSTNResult : taskResult.getStnResults()){
            if(jsonableSTNResult.getErrorList().size() == 0) continue;
            STNResult stnResult = new STNResult();
            stnResult.setId(jsonableSTNResult.getStnId());
            stnResult.setText(sentenceMap.get(jsonableSTNResult.getStnId()).getText());
            stnResults.put(stnResult.getId(), stnResult);
            stnResult.setStnResultList(jsonableSTNResult.getErrorList());
            stnResults.put(stnResult.getId(), stnResult);
        }

        List<STNResult> stnResultList = new ArrayList<>(stnResults.values());

        dataMap.put("errorStnList", stnResultList);

        return dataMap;

    }

    private Template getTemplate(String name){
        try {
            Configuration conf = new Configuration();

//            conf.setDirectoryForTemplateLoading(ResourceUtils.getFile("classpath:templates"));
            conf.setDirectoryForTemplateLoading(new java.io.File("./templates/"));
            return conf.getTemplate(name);

        } catch (IOException e){
            throw new RuntimeIOException(e.getMessage());
        }
    }

}
