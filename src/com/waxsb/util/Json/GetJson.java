package com.waxsb.util.Json;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class GetJson {
    public static JSONObject getJson(HttpServletRequest req){
        try {
            //1.获取数据
            BufferedReader br = req.getReader();
            String str, wholeStr = "";
            while((str = br.readLine()) != null){
                wholeStr += str;
            }
            JSONObject jsonObject = JSONObject.fromObject(wholeStr);
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
