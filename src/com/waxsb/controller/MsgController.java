package com.waxsb.controller;

import com.waxsb.model.SocketMsg;
import com.waxsb.model.User;
import com.waxsb.service.Impl.MsgContextServiceImpl;
import com.waxsb.service.MsgContextService;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.ResultInfo;
import com.waxsb.util.Page.PageBean;
import com.waxsb.util.Page.PageMsg;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/Msg/*")
public class MsgController extends BaseServlet {
    private static ResultInfo resInfo=new ResultInfo();
    private static ThreadLocal<ResultInfo> resultInfo=new ThreadLocal<ResultInfo>();
    public static ResultInfo getResultInfo() {
        resultInfo.set(resInfo);
        return resultInfo.get();
    }
    private static MsgContextService groupsSer= new MsgContextServiceImpl();
    private static ThreadLocal<MsgContextService> msgContextService=new ThreadLocal<MsgContextService>();
    public static MsgContextService getMsgContextService(){
        msgContextService.set(groupsSer);
        return msgContextService.get();
    }

    public void privateMsg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("根据用户名和好友名获取私聊记录");
        PageBean<SocketMsg> socketList = PageMsg.GetContent(req,resp);
        ResultInfo resultInfo=getResultInfo();
        resultInfo= ResultInfo.ResponseSuccess(socketList);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("查询流程结束");
    }
}
