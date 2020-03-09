package com.waxsb.util.Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class MyJson {
    public static JSONObject getJson(HttpServletRequest req){
        try {
            req.setCharacterEncoding("utf-8");
            //1.获取数据
            BufferedReader br = req.getReader();
            String str, wholeStr = "";
            while((str = br.readLine()) != null){
                wholeStr += str;
            }
            if(wholeStr==null||wholeStr==""){
                return null;
            }
            System.out.println("这是前端传回来的json字符串"+wholeStr);

    JSONObject jsonObject = JSONObject.fromObject(wholeStr);
            return jsonObject;
            } catch (IOException e) {
                e.printStackTrace();
         }
               return null;
    }

    public static void returnJson(ResultInfo resultInfo, HttpServletResponse resp) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        //将resultInfo对象序列化为json
        String json = mapper.writeValueAsString(resultInfo);
        //将json数据写回客户端
        //设置content-type
        resp.setContentType("application/json;character=utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(json);
    }
}
