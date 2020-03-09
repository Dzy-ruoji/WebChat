package com.waxsb.util.CheckCode;

import com.waxsb.model.User;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.ResultInfo;
import net.sf.json.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class MyCheckCode {
    public static void RespondCheckCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //服务器通知浏览器不要缓存
        resp.setHeader("pragma","no-cache");
        resp.setHeader("cache-control","no-cache");
        resp.setHeader("expires","0");

        //在内存中创建一个长80，宽30的图片，默认黑色背景
        //参数一：长
        //参数二：宽
        //参数三：颜色
        int width = 80;
        int height = 30;
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        //获取画笔
        Graphics g = image.getGraphics();
        //设置画笔颜色为灰色
        g.setColor(Color.GRAY);
        //填充图片
        g.fillRect(0,0, width,height);

        //产生4个随机验证码，
        String checkCode = getCheckCode();
        //将验证码放入HttpSession中
        req.getSession().setAttribute("CHECKCODE_SERVER",checkCode);

        //设置画笔颜色为黄色
        g.setColor(Color.YELLOW);
        //设置字体的小大
        g.setFont(new Font("黑体",Font.BOLD,24));
        //向图片上写入验证码
        g.drawString(checkCode,15,25);

        //将内存中的图片输出到浏览器
        //参数一：图片对象
        //参数二：图片的格式，如PNG,JPG,GIF
        //参数三：图片输出到哪里去
        ImageIO.write(image,"PNG",resp.getOutputStream());
    }

    public static void RespondMailCheckCode(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        //产生4个随机验证码，
        String checkCode = getCheckCode();
        //将验证码放入HttpSession中
        req.getSession().setAttribute("CHECKCODE_SERVER",checkCode);
        System.out.println("test：发送给邮箱的验证码为："+checkCode);
        User user=(User)req.getSession().getAttribute("user");
        String email=null;
        if(user==null){
            //没有user说明在注册页面，注册时需要读取表格中的邮箱，获取地址,前端页面也需要修改
            JSONObject json = MyJson.getJson(req);
             email = (String)json.get("email");
             System.out.println("注册email="+email);
        }else {
            email=user.getEmail();
            System.out.println("修改email="+email);
        }

        boolean flag=false;
        try {
           flag = MailUtils.sendMail(email, "您的验证码为" + checkCode, "验证信息");
      }catch (Exception e){
            e.printStackTrace();
            System.out.println("邮箱格式不正确");
      }


        ResultInfo resultInfo;
        if(checkCode!=""&&checkCode!=null&&flag==true){
             resultInfo = ResultInfo.ResponseSuccess("发送成功，请至邮箱查看验证码");
        }else{
            resultInfo=ResultInfo.ResponseFail("发送失败");
        }
        MyJson.returnJson(resultInfo,resp);
    }

    //产生4位随机字符串
    public static String getCheckCode() {
        String base = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int size = base.length();
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i=1;i<=4;i++){
            //产生0到size-1的随机值
            int index = r.nextInt(size);
            //在base字符串中获取下标为index的字符
            char c = base.charAt(index);
            //将c放入到StringBuffer中去
            sb.append(c);
        }
        return sb.toString();
    }

    public static JSONObject JudCheckCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject jsonObject = MyJson.getJson(req);
        req.setCharacterEncoding("utf-8");
        //获取json对象
        String checkcode = (String) jsonObject.get("checkcode");
        //从session中获取验证码
        HttpSession session = req.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        //比较，返回的时候验证码为空，会报异常
        if(checkcode_server==null){
            ResultInfo resultInfo = ResultInfo.ResponseFail("请输入验证码");
            MyJson.returnJson(resultInfo,resp);
            return null;
        }
        if(!checkcode_server.equalsIgnoreCase(checkcode)){
            ResultInfo resultInfo = ResultInfo.ResponseFail("验证码错误");
            MyJson.returnJson(resultInfo,resp);
            return null;
        }else {

            return jsonObject;
        }
    }
}
