package com.waxsb.controller;

import com.waxsb.model.User;
import com.waxsb.service.Impl.UserServiceImpl;
import com.waxsb.service.UserService;
import com.waxsb.util.CheckCode.MyCheckCode;
import com.waxsb.util.Image.MyImg;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.LockHelper;
import com.waxsb.util.Json.ResultInfo;
import com.waxsb.util.Page.PageBean;
import com.waxsb.util.Page.PageMsg;
import com.waxsb.util.listener.OnlineUsers;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/user/*")
public class UserController extends BaseServlet {
    ResultInfo resultInfo = new ResultInfo();
    OnlineUsers onlineUsers = new OnlineUsers();
    UserService userService = new UserServiceImpl();

    public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //登录逻辑
        System.out.println("login");
        //req.setCharacterEncoding("utf-8");
        JSONObject jsonObject = MyCheckCode.JudCheckCode(req, resp);
        if(jsonObject==null){
            return;
        }

        //验证码正确、封装对象
        User user=new User();
        user.setUsername((String) jsonObject.get("username"));
        user.setPassword((String) jsonObject.get("password"));
        //调用service完成登录

        User u=userService.login(user);
        if(u==null){
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("用户名或密码错误");
        }

        if(u!=null){
            List<User> userList = (List<User>) req.getSession().getServletContext().getAttribute("userList");//获取已登录的用户
            for(User userLogin : userList) {
                if(u.getUsername().equals(userLogin.getUsername())){
                    LockHelper.destroyedSession(userLogin);//销毁前一个用户
                    break;
                }
            }
            //登录成功
            req.getSession().setAttribute("user",u);
            onlineUsers.setUser(u);
            req.getSession().setAttribute("onlineUsers",onlineUsers);//感知到自己被绑定，触发绑定事件
            LockHelper.putSession(req.getSession());
             userList = (List<User>) req.getSession().getServletContext().getAttribute("userList");
            int size = userList.size();
            req.getServletContext().setAttribute("size",size);
            resultInfo.setFlag(true);
        }

        MyJson.returnJson(resultInfo,resp);
        System.out.println("login success");

    }

    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {

        JSONObject jsonObject = MyCheckCode.JudCheckCode(req, resp);
        if(jsonObject==null){
            return;
        }
        System.out.println("到这里了说明验证码对了...");

        //注册逻辑
        System.out.println("register...");

        //2.封装对象
        User user = new User();
        user.setUsername((String) jsonObject.get("username"));
        user.setName((String) jsonObject.get("name"));
        user.setPassword((String) jsonObject.get("password"));
        user.setEmail((String) jsonObject.get("email"));
        user.setTelephone((String) jsonObject.get("telephone"));
        user.setGender((String)jsonObject.get("gender"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = sdf.parse((String) jsonObject.get("birthday"));
        user.setBirthday(birthday);

        //3.调用service完成注册
        boolean flag =userService.register(user);
        //4.响应结果
        if(flag){
            //登录成功
            resultInfo.setFlag(true);

        }else{
            //注册失败
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("注册失败,用户名相同");
        }
        MyJson.returnJson(resultInfo,resp);
        System.out.println("注册流程结束");
    }

   public void checkCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       MyCheckCode.RespondCheckCode(req, resp);
   }

   //给邮箱发送验证码 注册/修改密码
   public void checkMailCode(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        MyCheckCode.RespondMailCheckCode(req,resp);
   }

    public void getUserList(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PageBean<User> userList = PageMsg.GetUserList(req, resp);
        resultInfo.setData(userList);
        resultInfo.setFlag(true);

        //将resultInfo对象序列化为json
        MyJson.returnJson(resultInfo,resp);
        System.out.println("查询流程结束");

    }

    public void getMyself(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //从session中获取登录信息
        User user = (User) req.getSession().getAttribute("user");
        String realPath=req.getServletContext().getRealPath("/Image");
        int i = realPath.indexOf("out\\artifacts\\__3__war_exploded\\Image");
        realPath = realPath.substring(0, i);
        realPath+="web\\Image";

        //返回头像图片名称
        String image_src = MyImg.getImage(user.getSrc(),realPath);
        user.setSrc(image_src);
        resultInfo.setData(user);

        MyJson.returnJson(resultInfo,resp);
        System.out.println("查询2流程结束");
    }

    public void updatePassword(HttpServletRequest req,HttpServletResponse resp) throws IOException {
       System.out.println("UpdatePassword");
        JSONObject jsonObject = MyCheckCode.JudCheckCode(req, resp);
        //JSONObject jsonObject = MyJson.getJson(req);
        if(jsonObject==null){
            return;
        }
        System.out.println("到这里了说明验证码对了...");
        //验证码正确
        //从session中获取登录信息，今天断网了测试不了
        User user = (User) req.getSession().getAttribute("user");

        String prePassword=(String) jsonObject.get("prePassword");//旧密码
        String newPassword=(String) jsonObject.get("newPassword");//新密码
        String newPassword1=(String) jsonObject.get("newPassword1");//确认新密码

        if(prePassword.equals(user.getPassword())&&newPassword.equals(newPassword1)){
            //调用sercivce层
            user=userService.updatePassword(user,newPassword);
            req.getSession().setAttribute("user",user);


        }else{
            if(!prePassword.equals(user.getPassword())){
                resultInfo= ResultInfo.ResponseFail("修改失败,原密码错误");
            }if(!newPassword.equals(newPassword1)){
                resultInfo =ResultInfo.ResponseFail("修改失败,新密码确认错误");
            }
        }

        MyJson.returnJson(resultInfo,resp);
    }

    public void updateMessage(HttpServletRequest req,HttpServletResponse resp) throws ParseException, IOException {
      System.out.println("updateMessage...");
        JSONObject jsonObject = MyJson.getJson(req);
        //传回了json数据
        if(jsonObject!=null){
            User user = new User();

            user.setUsername((String) jsonObject.get("username"));
            user.setName((String) jsonObject.get("name"));
            user.setTelephone((String) jsonObject.get("telephone"));
            user.setGender((String)jsonObject.get("gender"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthday = sdf.parse((String) jsonObject.get("birthday"));
            user.setBirthday(birthday);

            //3.调用service完成修改
             User u = userService.updateMessage(user);
            //4.响应结果
            if(u!=null){
                //修改成功
                resultInfo.setFlag(true);
            }else{
                //修改失败
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("修改失败,发生未知错误");
            }

        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("传输json数据时发生错误");
        }

        MyJson.returnJson(resultInfo,resp);

    }

    public void uploadImg(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        JSONObject jsonObject = MyJson.getJson(req);
        //获取前端传回的绝对路径
        String src = (String)jsonObject.get("src");
        String realPath=req.getServletContext().getRealPath("/Image");
        int i = realPath.indexOf("out\\artifacts\\__3__war_exploded\\Image");
        realPath = realPath.substring(0, i);
        realPath+="web\\Image";

        //写入文件，修改名称
        String image_src = MyImg.uploadImage(src,realPath,user.getId());
        //调用service储存新的image_src
        userService.updateImg(user.getId(),image_src);
        //传回修改后的头像路径
        resultInfo.setData(image_src);
        MyJson.returnJson(resultInfo,resp);

    }
}

