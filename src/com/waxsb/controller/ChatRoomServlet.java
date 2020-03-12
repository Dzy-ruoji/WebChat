package com.waxsb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;
import com.waxsb.model.User;
import com.waxsb.service.Impl.FriendServiceImpl;
import com.waxsb.service.Impl.GroupsServiceImpl;
import com.waxsb.model.SocketMsg;
import com.waxsb.service.Impl.MsgContextServiceImpl;
import com.waxsb.util.Json.WsInfo;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
//前端需要判断消息的类型为纯文本、文件、图片
@ServerEndpoint("/chat/chatRoomSerController/{myname}")
public class ChatRoomServlet {
    protected Session session;
    protected String username;
    //记录此次聊天室有多少个连接 key是SessionId value是ChatRoomServer对象
    protected static  ConcurrentMap<String,Object> connectMap = new ConcurrentHashMap<String,Object>();
    //根据用户名寻找session
    protected static  ConcurrentMap<String,Session> UserSession = new ConcurrentHashMap<String,Session>();
    //储存离线信息
    protected static Map<String, Map<String,List<SocketMsg>>> messageMap=new HashMap<>();
    //储存信息的集合
    protected static CopyOnWriteArrayList<SocketMsg> list = new CopyOnWriteArrayList();
    protected static ConcurrentMap<String,GroupsServiceImpl> GroupsServiceImplMap = new ConcurrentHashMap<>();
    protected static ConcurrentMap<String,ObjectMapper> ObjectMapper = new ConcurrentHashMap<>();
    protected static ConcurrentMap<String,MsgContextServiceImpl> MsgContextServiceImplMap = new ConcurrentHashMap<>();
    protected static ConcurrentMap<String,FriendServiceImpl> FriendServiceImplMap = new ConcurrentHashMap<>();
    //服务端收到客户端连接请求，连接成功后会执行此方法
    @OnOpen
    public void start(Session session,@PathParam("myname") String username){//不是Http协议里的session
        this.session = session;
        this.username=username;
        MsgContextServiceImplMap.put(session.getId(),new MsgContextServiceImpl());
        GroupsServiceImplMap.put(session.getId(),new GroupsServiceImpl());
        FriendServiceImplMap.put(session.getId(),new FriendServiceImpl());
        ObjectMapper.put(session.getId(),new ObjectMapper());
        //key为sessionId、value为ChatRoomServer对象
        connectMap.put(session.getId(),this);
        UserSession.put(username,session);
    }

    @OnMessage
    public void chat(String clientMessage,Session session,@PathParam("myname") String username) {
        //从客户端传过来的数据是json数据，所以这里使用jackson进行转换为SocketMsg对象，
        // 然后通过socketMsg的type进行判断是单聊还是群聊，进行相应的处理:
        ObjectMapper objectMapper = ObjectMapper.get(session.getId());
        WsInfo wsInfo  = null;
        SocketMsg socketMsg = null;
        try {
            wsInfo = objectMapper.readValue(clientMessage, WsInfo.class);
            socketMsg = wsInfo.getSocketMsg();
            java.util.Date date = new java.util.Date();          // 获取一个Date对象
            Timestamp timeStamp = new Timestamp(date.getTime());//   讲日期时间转换为数据库中的timestamp类型
            socketMsg.setCreateTime(timeStamp);

            //将socketMsg存入数据库中的消息表
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("前端传输json格式错误");
        }

        // 获取指定类的字节码对象
        ChatRoomController chatRoomController = new ChatRoomController();//可修改
        Class<? extends ChatRoomServlet> clazz = chatRoomController.getClass();//这里的this指的是继承BaseServlet对象
        // 通过类的字节码对象获取方法的字节码对象
        Method method = null;
        try {
            method = clazz.getMethod(wsInfo.getMethodName(),WsInfo.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        // 让方法执行
        try {
            method.invoke(chatRoomController,wsInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void close(Session session){
        //当用户退出时，对其他用户进行广播
        ChatRoomServlet chatRoomServer = (ChatRoomServlet) connectMap.get(session.getId());
        String message = "系统消息:"+chatRoomServer.username+"退出了聊天室";
        connectMap.remove(session.getId());
        UserSession.remove(username);
        GroupsServiceImplMap.remove(username);
        ObjectMapper.remove(username);
        //将消息广播给所有用户
        System.out.println(message);
    }

    @OnError
    public void error(Session session, Throwable error){
        System.out.println("连接发生错误");
        error.printStackTrace();
    }





    //反射方法
    public void invoke(WsInfo wsInfo, Session session, @PathParam("myname") String username) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 获取指定类的字节码对象
        Class<? extends ChatRoomServlet> clazz = this.getClass();//这里的this指的是继承BaseServlet对象
        // 通过类的字节码对象获取方法的字节码对象
        Method method = clazz.getDeclaredMethod(wsInfo.getMethodName(), getMethodParamTypes(this, wsInfo.getMethodName()));
        // 让方法执行
        method.invoke(this, wsInfo.getParameters());
    }

    public static Class[]  getMethodParamTypes(Object classInstance, String methodName) throws ClassNotFoundException{
        Class[] paramTypes = null;
        // classInstance实例类
        Method[]  methods = classInstance.getClass().getMethods();//全部方法
        for (int  i = 0;  i< methods.length; i++) {
            if(methodName.equals(methods[i].getName())){//和传入方法名匹配
                Class[] params = methods[i].getParameterTypes();
                paramTypes = new Class[ params.length] ;
                for (int j = 0; j < params.length; j++) {
                    paramTypes[j] = Class.forName(params[j].getName());
                }
                break;
            }
        }
        return paramTypes;
    }
}
