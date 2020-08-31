package org.codedream.epaper.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.codedream.epaper.component.api.QuickJSONRespond;
import org.codedream.epaper.component.json.respond.ErrorInfoJSONRespond;
import org.codedream.epaper.exception.badrequest.AlreadyExistException;
import org.codedream.epaper.exception.badrequest.IllegalException;
import org.codedream.epaper.exception.conflict.RelatedObjectsExistException;
import org.codedream.epaper.exception.innerservererror.FormatException;
import org.codedream.epaper.exception.innerservererror.HandlingErrorsException;
import org.codedream.epaper.exception.innerservererror.RuntimeIOException;
import org.codedream.epaper.exception.notfound.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 错误/异常管理机制控制
 */
@Slf4j
@RestControllerAdvice
@Api(hidden = true)
public class EPControllerAdvice {

    @Resource
    private QuickJSONRespond quickJSONRespond;

    /**
     * 非法请求类异常处理
     * @param ex 异常
     * @return 返回对象
     */
    @ExceptionHandler(value = {
            NullPointerException.class,
            AlreadyExistException.class,
            IllegalException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception ex) {
        ex.printStackTrace();
        return getResponse(HttpStatus.BAD_REQUEST, ex);
    }

    /**
     * 未找到类异常处理
     * @param ex 异常
     * @return 返回对象
     */
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFound(Exception ex) {

        return getResponse(HttpStatus.NOT_FOUND, ex);
    }

    /**
     * 不可接受类异常处理
     * @param ex 异常
     * @return 返回对象
     */
    @ExceptionHandler(value = {})
    public ResponseEntity<Object> handleNotAcceptable(Exception ex) {
        return getResponse(HttpStatus.NOT_ACCEPTABLE, ex);
    }

    /**
     * 冲突类异常处理
     * @param ex 异常
     * @return 返回对象
     */
    @ExceptionHandler(value = {RelatedObjectsExistException.class})
    public ResponseEntity<Object> handleConflict(Exception ex) {
        return getResponse(HttpStatus.CONFLICT, ex);
    }

    /**
     * 内部错误类异常处理
     * @param ex 异常
     * @return 返回对象
     */
    @ExceptionHandler(value = {
            HandlingErrorsException.class,
            FormatException.class,
            RuntimeIOException.class})
    public ResponseEntity<Object> handleInnerServerError(Exception ex){
        return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    /**
     * 构造JSON填充的返回对象
     * @param status HTTP状态码
     * @param ex 异常
     * @return 返回对象
     */
    private ResponseEntity<Object> getResponse(HttpStatus status, Exception ex){
        return ResponseEntity.status(status).body(getJSON(status, ex));

    }

    private String getJSON(HttpStatus status, Exception ex){
        return quickJSONRespond.getJSONStandardRespond(status, getJSONRespondObject(ex));
    }

    private Object getJSONRespondObject(Exception ex){
        ErrorInfoJSONRespond errorInfoJSONRespond = new ErrorInfoJSONRespond();
        errorInfoJSONRespond.setException(ex.getClass().getName());
        errorInfoJSONRespond.setExceptionMessage(ex.getMessage());
        errorInfoJSONRespond.setDate(new Date());
        return errorInfoJSONRespond;
    }


}
