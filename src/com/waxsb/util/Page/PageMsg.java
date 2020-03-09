package com.waxsb.util.Page;

import com.waxsb.model.SocketMsg;
import com.waxsb.model.User;
import com.waxsb.model.User_GroupsMSGContent;
import com.waxsb.service.Impl.MsgContextServiceImpl;
import com.waxsb.service.Impl.UserServiceImpl;
import com.waxsb.service.MsgContextService;
import com.waxsb.service.UserService;
import com.waxsb.util.Json.MyJson;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.sql.Date;

public class PageMsg {
    public static PageBean<User> GetUserList(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //1.获取参数
        String currentPage=null;
        String row=null;
        request.setCharacterEncoding("utf-8");
        JSONObject jsonObject = MyJson.getJson(request);

        if(jsonObject!=null){
          currentPage =jsonObject.get("currentPage").toString();//传递当前页码
            row = jsonObject.get("row").toString();//每页显示条数
        }

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

    public static PageBean<User> GetUserByUserName(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //1.获取参数
        String currentPage=null;
        String row=null;
        String username=null;
        request.setCharacterEncoding("utf-8");
        JSONObject jsonObject = MyJson.getJson(request);

        if(jsonObject!=null){
            currentPage =jsonObject.get("currentPage").toString();//传递当前页码
            row = jsonObject.get("row").toString();//每页显示条数

            try {
                username=jsonObject.get("username").toString();//查询的用户名
            } catch (Exception e) {
                System.out.println("查询不带用户名");
            }

        }

        if(currentPage==null||"".equals(currentPage)){
            currentPage="1";
        }
        if(row==null||"".equals(row)){
            row="5";
        }


        //2.调用service查询
        UserService service=new UserServiceImpl();
        PageBean<User>pb= service.findUserBySearchName(currentPage,row,username);
        /*//3.将PageBean存入request
        request.setAttribute("pb",pb);
*/
        //将PageBean转化成json传给首页
        return pb;
    }
    //查询聊天记录
    public static PageBean<SocketMsg> GetContent(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //1.获取参数
        User user = (User) request.getSession().getAttribute("user");
        MsgContextService MsgService = new MsgContextServiceImpl();
        PageBean<SocketMsg>pb = null;
        int currentPage = 0;
        int row = 0;
        int type = -1; //聊天类型：2群聊，1：单聊.
        String fromUser = user.getUsername();//发送者
        String toUser;//接收者.
        int user_GroupID;//群id
        int user_nickname;//群昵称
        int msgType;//消息类型
        Date date=null;//获取时间
        request.setCharacterEncoding("utf-8");
        JSONObject jsonObject = MyJson.getJson(request);

        if(jsonObject!=null){
            currentPage = (int) jsonObject.get("currentPage");//传递当前页码
            row = (int) jsonObject.get("row");//每页显示条数
            type= (int) jsonObject.get("type");//聊天类型
        }

        if(currentPage<=0){
            currentPage=1;
        }
        if(row<=0){
            row=5;
        }

        if(type == 2){
            //这是群聊
            user_GroupID= (int) jsonObject.get("groupId");//群id
            pb = MsgService.findPublicMsg(currentPage,row,user_GroupID,date);
        }else if(type == 1){
            //这是私聊
           toUser = (String) jsonObject.get("toUser");
           //在表中不确定谁是发送者谁是接收者，是双向的
            pb = MsgService.findPrivateMsg(currentPage,row,fromUser,toUser,date);

        }else {
            System.out.println("出问题了，啥也不是");
        }

        return pb;
    }
}
