package org.codedream.epaper.component.datamanager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.codedream.epaper.exception.innerservererror.HandlingErrorsException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Optional;

/**
 * JSON请求参数处理器
 */
@Component
public class JSONParameter {

    /**
     * 获得HTTP请求内容
     * @param request HTTP请求
     * @return 内容字符串
     */
    public  String getRequestBody(HttpServletRequest request){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = request.getReader();
            reader.reset();
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 提取Request内容并解析为JSON对象
     * @param request HTTP请求
     * @return JSON对象
     */
    public Optional<JSONObject> getJSONByRequest(HttpServletRequest request){
        try {
            JSONObject jsonParam = null;
            String content = getRequestBody(request);
            jsonParam = JSONObject.parseObject(content);
            return Optional.ofNullable(jsonParam);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    /**
     * 根据JSON对象构造JSON字符串用于返回
     * @param json JSON对象
     * @return JSON字符串
     */
    public String getJSONString(JSONObject json){
        return json.toJSONString();
    }

    /**
     * 根据Java对象构造JSON字符串用于返回
     * @param object Java对象
     * @return JSON字符串
     */
    public String getJSONString(Object object){
        return JSON.toJSONString(object);
    }

    /**
     * 由JSON对象获得对应的Java对象
     * @param json JSON对象
     * @param type 对应的Java对象类型
     * @param <T> 对应的Java对象类型
     * @return 指定的Java对象
     */
    public <T> T getJavaObject(JSONObject json, Class<T> type){
        return json.toJavaObject(type);
    }

    /**
     * 由HTTP请求获得对应的Java对象(一般用于Post请求中)
     * @param request HTTP请求
     * @param type 对应的Java对象类型
     * @param <T> 对应的Java对象类型
     * @return 指定的Java对象
     */
    public <T> Optional<T> getJavaObjectByRequest(HttpServletRequest request, Class<T> type){
        Optional<JSONObject> json = getJSONByRequest(request);
        return json.map(jsonObject -> getJavaObject(jsonObject, type));
    }

    /**
     * 将JsonPath对象转换成Java对象（Restful API的Path动词）
     * @param patch JsonPath对象
     * @param object Java对象
     * @param <T> 对应的Java对象类型
     * @return 指定的Java对象（更新后）
     */
    public <T> T parsePathToObject(JsonPatch patch, T object){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode patched = patch.apply(mapper.convertValue(object, JsonNode.class));
            return (T) mapper.treeToValue(patched, object.getClass());
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new HandlingErrorsException(e.getMessage());
        }

    }

}
