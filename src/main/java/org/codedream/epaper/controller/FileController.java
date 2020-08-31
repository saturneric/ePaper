package org.codedream.epaper.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hdgf.streams.StringsStream;
import org.codedream.epaper.component.json.model.JsonableFile;
import org.codedream.epaper.configure.AppConfigure;
import org.codedream.epaper.exception.badrequest.IllegalException;
import org.codedream.epaper.exception.innerservererror.HandlingErrorsException;
import org.codedream.epaper.exception.innerservererror.RuntimeIOException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.codedream.epaper.model.file.File;
import org.codedream.epaper.service.FileService;
import org.codedream.epaper.service.IFileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Optional;

@Slf4j
@RestController
@Api("文件服务类接口")
@RequestMapping("file")
public class FileController {

    @Resource
    private IFileService fileService;

    @Resource
    private AppConfigure configure;

    @PostMapping("text")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("文本上传接口")
    public JsonableFile uploadText(@RequestBody String text) {
        if(text == null || text.length() < 300) throw new IllegalArgumentException();
        ByteArrayInputStream stream = new ByteArrayInputStream(text.getBytes());

        Integer fileId =  fileService.saveFile(text.substring(0, 12), "plain", stream);

        JsonableFile jsonableFile = new JsonableFile();
        jsonableFile.setFileId(fileId);
        jsonableFile.setFilename(text.substring(0, 12));
        jsonableFile.setType("plain");
        return jsonableFile;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("文件上传接口")
    public JsonableFile uploadFile(@RequestParam("file") MultipartFile file){
        String filename = file.getOriginalFilename();

        String[] strArray = filename.split("\\.");
        int suffixIndex = strArray.length -1;
        String fileType = strArray[suffixIndex];

        log.info(String.format("File Upload filename %s", filename));
        log.info(String.format("File Upload fileType %s", fileType));

        // 检查文件大小
        if(file.getSize() > configure.getFileMaxSize()) throw new IllegalException(Long.toString(file.getSize()));

        if(fileType.equals("doc") || fileType.equals("docx") || fileType.equals("pdf")){
            try {
                byte[] fileData = file.getBytes();
                ByteArrayInputStream stream = new ByteArrayInputStream(fileData);
                Integer fileId =  fileService.saveFile(filename, fileType, stream);

                // 填写返回JSON
                JsonableFile jsonableFile = new JsonableFile();
                jsonableFile.setFileId(fileId);
                jsonableFile.setFilename(filename);
                jsonableFile.setType(fileType);
                return jsonableFile;

            } catch (IOException e){
                throw new RuntimeIOException(filename);
            }

        }
        else throw new IllegalException(fileType);
    }

    @GetMapping("download")
    public void downloadFile(@RequestParam("fileId") Integer fileId, HttpServletResponse response){
        File file = fileService.getFileInfo(fileId);
        if(file == null) throw new NotFoundException(fileId.toString());

        try {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }

        InputStream stream = fileService.getFile(fileId);

        try{
            OutputStream outputStream = response.getOutputStream();
            int readBits;
            byte[] rawBytes = new byte[1024];
            while ((readBits = stream.read(rawBytes)) != -1) {
                outputStream.write(rawBytes, 0, readBits);
            }
            outputStream.close();
            stream.close();
        } catch (Exception e){
            throw new HandlingErrorsException(e.getMessage());
        }

    }
}
