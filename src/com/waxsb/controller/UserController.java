package com.waxsb.controller;

import com.waxsb.model.User;
import com.waxsb.service.Impl.UserServiceImpl;
import com.waxsb.service.UserService;
import com.waxsb.util.CheckCode.MyCheckCode;
import com.waxsb.util.File.MyFile;
import com.waxsb.util.Image.MyImg;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.ResultInfo;
import com.waxsb.util.Page.PageBean;
import com.waxsb.util.Page.PageMsg;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/user/*")
public class UserController extends BaseServlet {
    private static ResultInfo resInfo=new ResultInfo();
    private static UserService userSer= new UserServiceImpl();
    private static ThreadLocal<ResultInfo> resultInfo=new ThreadLocal<ResultInfo>();
    private static ThreadLocal<UserService> userService=new ThreadLocal<UserService>();
    public static ResultInfo getResultInfo() {
        resultInfo.set(resInfo);
        return resultInfo.get();
    }
    public static UserService getUserService() {
        userService.set(userSer);
        return userService.get();
    }

    public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //登录逻辑
        System.out.println("login");
        JSONObject jsonObject = MyCheckCode.JudCheckCode(req, resp);
        if(jsonObject==null){
            return;
        }
        ResultInfo resultInfo = getResultInfo();
        String username=(String) jsonObject.get("username");
        String password=(String) jsonObject.get("password");
        if(username==null||password==null){
           resultInfo=ResultInfo.ResponseFail("用户名或密码错误");
           MyJson.returnJson(resultInfo,resp);
           return;
        }
        //验证码正确、封装对象
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        //调用service完成登录
        UserService userService =getUserService();
        User u = userService.login(user);


        if(u==null){
            System.out.println("用户名或密码错误");
            resultInfo = ResultInfo.ResponseFail("用户名或密码错误");
        }else{
            //登录成功
            //同一浏览器：第二个用户登录后将之前的session销毁！
            req.getSession().invalidate();
            //不同浏览器：判断用户是否已经在map集合中，存在，销毁session
            //得到ServletContext中存的map集合
            Map<User,HttpSession> userMap = (Map<User, HttpSession>) getServletContext().getAttribute("userMap");
            //浏览器不同session不同，即key不同，重写后，比较的是id的hashcode
           if(userMap.containsKey(u)){
               //说明map中有这个用户
               HttpSession session = userMap.get(u);
               //将session销毁
               session.invalidate();
           }

            //使用监听器：HttpSessionBandingListener
            req.getSession().setAttribute("user",u);
            resultInfo = ResultInfo.ResponseSuccess();
        }

        MyJson.returnJson(resultInfo,resp);
        System.out.println("login success");

    }

    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {

        JSONObject jsonObject = MyCheckCode.JudCheckCode(req, resp);
        if(jsonObject==null){
            return;
        }

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
        UserService userService = getUserService();
        ResultInfo resultInfo = getResultInfo();
        boolean flag = userService.register(user);
        //4.响应结果
        if(flag){
            //登录成功
             resultInfo = ResultInfo.ResponseSuccess();
        }else{
            //注册失败
            resultInfo = ResultInfo.ResponseFail("注册失败，用户名相同");
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
        ResultInfo resultInfo=getResultInfo();
        resultInfo= ResultInfo.ResponseSuccess(userList);
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

        if(user==null){
            System.out.println("被踢了或者没登录，稍后处理");
        }
        //返回头像图片名称
        String image_src = MyImg.getImage(user.getSrc(),realPath);
        ResultInfo resultInfo = getResultInfo();
        user.setSrc(image_src);
        resultInfo=ResultInfo.ResponseSuccess(user);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("查询2流程结束");
    }

    public void updatePassword(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("UpdatePassword");
        UserService userService = getUserService();
        ResultInfo resultInfo = getResultInfo();
        JSONObject jsonObject = MyCheckCode.JudCheckCode(req, resp);
        if(jsonObject==null){
            return;
        }
        //验证码正确
        User user = (User) req.getSession().getAttribute("user");
        String prePassword=(String) jsonObject.get("prePassword");//旧密码
        String newPassword=(String) jsonObject.get("newPassword");//新密码
        String newPassword1=(String) jsonObject.get("newPassword1");//确认新密码

        if(prePassword.equals(user.getPassword())&&newPassword.equals(newPassword1)){
            //调用sercivce层
            user= userService.updatePassword(user,newPassword);
            req.getSession().setAttribute("user",user);
            resultInfo=ResultInfo.ResponseSuccess();
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
        UserService userService = getUserService();
        ResultInfo resultInfo = getResultInfo();

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
             //考虑到监听器，先移除，后覆盖
             req.getSession().removeAttribute("user");
             req.getSession().setAttribute("user",u);
            //4.响应结果
            if(u!=null){
                //修改成功
                 resultInfo = ResultInfo.ResponseSuccess(u);
            }else{
                //修改失败
                resultInfo=ResultInfo.ResponseFail("修改失败，发生未知错误");
            }

        }else {
            resultInfo=ResultInfo.ResponseFail("传输json数据时发生错误");
        }
        MyJson.returnJson(resultInfo,resp);
    }

    public void uploadImg(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        UserService userService = getUserService();
        ResultInfo resultInfo = getResultInfo();
        MyFile myFile = new MyFile();

        //写入文件，修改名称
        String image_src=myFile.uploadFile(req);

        if(image_src==null){
            resultInfo = ResultInfo.ResponseFail("发生未知错误");
            MyJson.returnJson(resultInfo,resp);
            return;
        }

        //调用service储存新的image_src
        userService.updateImg(user.getId(),image_src);
        //传回修改后的头像路径
        resultInfo = ResultInfo.ResponseSuccess(image_src);
        //能得到什么路径
        MyJson.returnJson(resultInfo,resp);
    }

    public  void findUserBySearchName(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        PageBean<User> userList = PageMsg.GetUserByUserName(req,resp);
        ResultInfo resultInfo=getResultInfo();
        resultInfo= ResultInfo.ResponseSuccess(userList);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("查询流程结束");
    }

    public void download(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        //修改响应的头部属性
       resp.setHeader("content-disposition","attachment");
       //获取连接服务器端资源文件的输入流
       InputStream is = this.getServletContext().getResourceAsStream("");
       //获取输出流
       ServletOutputStream os = resp.getOutputStream();
       //将输入流中的数据写出到输出流中
       int len = -1;
       byte [] buf =  new byte[1024];
       while ((len=is.read(buf))!=-1){
           os.write(buf,0,len);
       }
       //关闭流
       os.close();
       is.close();
   }

    //测试
    //在线踢人
    public  void kick(HttpServletRequest req,HttpServletResponse resp){
        int id=Integer.parseInt(req.getParameter("id"));
        //踢人，从userMap集合中将对于的session销毁
        Map<User,HttpSession> userMap = (Map<User, HttpSession>) getServletContext().getAttribute("userMap");
        //重写user的equals和hashcode方法 那么只要用户id相同默认同一用户
        User user = new User();
        user.setId(id);
        //从map集合中获得对应用户的session
        HttpSession session = userMap.get(user);
        //销毁session
        session.invalidate();
    }

    //获取消息
    public void getMessage(HttpServletRequest req,HttpServletResponse resp){
        System.out.println("get message..");
        String message = (String) getServletContext().getAttribute("message");

        if(message!=null){
            try {
                resp.getWriter().println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //发送聊天内容
    public void sendMessage(HttpServletRequest req,HttpServletResponse resp){
        //1.获取数据 发言人，表情，接收者，字体颜色，发言内容，发言时间
        //2.获得ServletContext对象
        //3.从ServletContext中获取信息


    }

    //退出聊天室
    public void exit(HttpServletRequest req,HttpServletResponse resp){
        HttpSession session = req.getSession();
        session.invalidate();
        //回到登录页面
    }

    //检测session是否过期
    public void check(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        if(user==null){
            //过期了
            resp.getWriter().println("1");
        }else{
            resp.getWriter().println("2");
        }
    }
}

