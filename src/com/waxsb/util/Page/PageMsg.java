package com.waxsb.util.Page;

import com.waxsb.model.User;
import com.waxsb.service.Impl.UserServiceImpl;
import com.waxsb.service.UserService;
import com.waxsb.util.Json.GetJson;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class PageMsg {
    public static PageBean<User> GetUserList(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //1.获取参数

        request.setCharacterEncoding("utf-8");
        JSONObject jsonObject = GetJson.getJson(request);
        String currentPage = (String) jsonObject.get("currentPage");//传递当前页码
        String row = (String) jsonObject.get("row");//每页显示条数

        if(currentPage==null||"".equals(currentPage)){
            currentPage="1";
        }
        if(row==null||"".equals(row)){
            row="5";
        }


        //2.调用service查询
        UserService service=new UserServiceImpl();
        PageBean<User>pb= service.findUserByPage(currentPage,row);
        /*//3.将PageBean存入request
        request.setAttribute("pb",pb);
*/
        //将PageBean转化成json传给首页
        return pb;


    }
}
