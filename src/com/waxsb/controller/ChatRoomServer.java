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
import net.sf.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
//前端需要判断消息的类型为纯文本、文件、图片
@ServerEndpoint("/chat/chatRoomServer/{myname}")
public class ChatRoomServer {
    private Session session;
    private String username;
    //记录此次聊天室有多少个连接 key是SessionId value是ChatRoomServer对象
    private static  ConcurrentMap<String,Object> connectMap = new ConcurrentHashMap<String,Object>();
    //根据用户名寻找session
    private static  ConcurrentMap<String,Session> UserSession = new ConcurrentHashMap<String,Session>();
    //储存离线信息
    private static Map<String, Map<String,List<SocketMsg>>> messageMap=new HashMap<>();

    //储存信息的集合
    private static CopyOnWriteArrayList list = new CopyOnWriteArrayList();
    private static ConcurrentMap<String,GroupsServiceImpl> GroupsServiceImplMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String,ObjectMapper> ObjectMapper = new ConcurrentHashMap<>();
    private static ConcurrentMap<String,MsgContextServiceImpl> MsgContextServiceImplMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String,FriendServiceImpl> FriendServiceImplMap = new ConcurrentHashMap<>();
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
    public void chat(String clientMessage,Session session,@PathParam("myname") String username){
        //从客户端传过来的数据是json数据，所以这里使用jackson进行转换为SocketMsg对象，
        // 然后通过socketMsg的type进行判断是单聊还是群聊，进行相应的处理:
        ObjectMapper objectMapper = ObjectMapper.get(session.getId());
        MsgContextServiceImpl msgContextService1 = MsgContextServiceImplMap.get(session.getId());
        SocketMsg socketMsg = null;
        try {
            socketMsg = objectMapper.readValue(clientMessage,SocketMsg.class);
            java.util.Date date = new java.util.Date();          // 获取一个Date对象
            Timestamp timeStamp = new Timestamp(date.getTime());//   讲日期时间转换为数据库中的timestamp类型
            socketMsg.setCreateTime(timeStamp);
            //将socketMsg存入数据库中的消息表
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("前端传输json格式错误");
        }
        //存储信息
        saveMsg(socketMsg);

        if(socketMsg.getType() == 1){
            msgContextService1.insertContext(socketMsg);
            //私聊，需要找到发送者和接受者
            String fromUsername = socketMsg.getFromUser();//发送者用户名
            String toUsername = socketMsg.getToUser();//接收者用户名
            Session fromSession = this.session;
            //通过接收者用户名寻找session
            Session toSession = UserSession.get(toUsername);
            if(toSession!=null){
                //发送给接受者和接收者 返回对象
                privateChat(socketMsg,fromSession,toSession,3);
            }else {
                //发给自己
                privateChat(socketMsg,fromSession,toSession,1);
            }
        }else if(socketMsg.getType() == 2){
            //根据群id群发，并插入
            msgContextService1.insertContext(socketMsg);
            broadcast(socketMsg,socketMsg.getUser_GroupID());
        }else {
            //添加好友请求
            addFriend(socketMsg);
        }

    }

    @OnClose
    public void close(Session session){
        //当用户退出时，对其他用户进行广播
        ChatRoomServer chatRoomServer = (ChatRoomServer) connectMap.get(session.getId());
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

    public void broadcast(SocketMsg socketMsg,int UG_GroupId){
        //根据群id获取所有用户名信息
        GroupsServiceImpl groupsService = GroupsServiceImplMap.get(session.getId());
        ObjectMapper mapper = ObjectMapper.get(session.getId());
        //发一条信息就要查询一次数据库 优化（查询一个数据库后存到map集合中，等到有人退出或者加入时谁同意谁更新map集合）
        List<User> memberName = groupsService.memberName(UG_GroupId);
        if(memberName!=null){
            //根据用户名获得sessionid发送
            for(User name : memberName){
                //获取当前群中用户的session,包括发送者自己
                Session session = UserSession.get(name.getUsername());
                if(session!=null){//在线
                    String json = null;
                    //将resultInfo对象序列化为json
                    try {
                        json = mapper.writeValueAsString(socketMsg);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    //将json数据写回客户端
                    try {
                        session.getBasicRemote().sendText("notReadTotal&"+getNotReadTotal(socketMsg.getToUser()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    session.getAsyncRemote().sendText(json);
                }
            }
        }
    }

    public void privateChat(SocketMsg socketMsg,Session fromSession,Session toSession,int type){
        ObjectMapper mapper = ObjectMapper.get(session.getId());
        String json = null;
        //将resultInfo对象序列化为json
        try {
            json = mapper.writeValueAsString(socketMsg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //将json数据写回客户端
        if(type==1){
            //返回给自己
            fromSession.getAsyncRemote().sendText(json);
        }else if(type==2){
            //发送给对方
            if(toSession!=null){
                try {
                    toSession.getBasicRemote().sendText("notReadTotal&"+getNotReadTotal(socketMsg.getToUser()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toSession.getAsyncRemote().sendText(json);
                 }

        } else {
            //发送给双方
            fromSession.getAsyncRemote().sendText(json);
            if(toSession!=null){
                try {
                    toSession.getBasicRemote().sendText("notReadTotal&"+getNotReadTotal(socketMsg.getToUser()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toSession.getAsyncRemote().sendText(json);
                //同步发送出错
                  }
        }
    }

    /** 添加离线消息到Map到 */
    public void saveMsg(SocketMsg socketMsg){
        //用户不在线，将消息存入表中并且存入map集合中

        //key是接收方的用户名，value是待接收的list集合
        //map :  key(接收者) value(map(key:发送者:value(是这个好友发送给该接收者的所有消息)))
            Map<String,List<SocketMsg>>  out = null;
            if(socketMsg.getType() == 1 || socketMsg.getType() == 3) { //好友消息
                out = messageMap.get(socketMsg.getToUser());
            }else if(socketMsg.getType() == 2){
                out = messageMap.get(socketMsg.getUser_GroupID());
            }
            if(out == null){
                out = new HashMap<String,List<SocketMsg>>();
            }
            List<SocketMsg> list  = out.get(socketMsg.getFromUser());
            if(list == null){
                list = new ArrayList<SocketMsg>();
            }
            list.add(socketMsg);
            out.put(socketMsg.getFromUser(),list);
            messageMap.put(socketMsg.getToUser(),out);
    }
    /** 取出对应好友或群未读消息个数 */
    public Integer getNotReadCount(SocketMsg socketMsg){
        String key = socketMsg.getType() == 1?socketMsg.getToUser():socketMsg.getUser_GroupID()+"";
        Map<String, List<SocketMsg>> map = messageMap.get(key);
        return map.get(socketMsg.getFromUser())== null?0:map.get(socketMsg.getFromUser()).size();
    }
    /** 取所有长度 */
    public Integer getNotReadTotal(String Sname){
        int total = 0;
        Map<String, List<SocketMsg>> map = messageMap.get(Sname);
        if(map!=null){
            for(String key : map.keySet()){
                total += map.get(key).size();
            }
        }
        return total;
    }
    /** 从未读Map中删除已读消息 */
    public void delete(String key1,String key2){//key(接收者) value: key(发送者) value
        messageMap.get(key1).remove(key2);
    }


    public void addFriend(SocketMsg socketMsg){
        boolean flag=true;
        FriendServiceImpl friendService = FriendServiceImplMap.get(session.getId());
        if(!socketMsg.getToUser().equals(socketMsg.getFromUser())){
            AddFriend addFriend=new AddFriend();
            Session fromSession = this.session;
            //通过接收者用户名寻找session
            Session toSession = UserSession.get(socketMsg.getToUser());
            //获取当前用户好友信息
            List<Friend> friends= friendService.findFriendByName(socketMsg.getFromUser());
            if(friends!=null){
                for(Friend friend:friends){
                    if(friend.getFriend_1().equals(socketMsg.getFromUser())){
                        //friend.getFriend_2()是好友
                        if(friend.getFriend_2().equals(socketMsg.getToUser())){
                            //已经是好友
                            socketMsg.setMsg("请勿重复添加好友");
                            socketMsg.setMsgType(5);
                            //两个private
                            privateChat(socketMsg,fromSession,toSession,1);
                            flag=false;
                            break;
                        }
                    }else{
                        //friend.getFriend_1()是好友
                        if(friend.getFriend_1().equals(socketMsg.getToUser())){
                            //已经是好友
                            socketMsg.setMsg("请勿重复添加好友");
                            socketMsg.setMsgType(5);
                            //两个private
                            privateChat(socketMsg,fromSession,toSession,1);
                            flag=false;
                            break;
                        }
                    }
                }
            }
            if(flag){
                //到这里说明不是好友（可能之前发送过请求，不过对方没同意，Friend表中没）
                addFriend.setFriend_1(socketMsg.getFromUser());
                addFriend.setFriend_2(socketMsg.getToUser());
                addFriend.setF1_allow(AddFriend.getALLOW());
                //因为是发送添加好友请求，所以接收请求方F2_allow默认为空
                //将数据插入addFriend表
                friendService.addFriend(addFriend);
                //两个privateChat
                privateChat(socketMsg,fromSession,toSession,2);
            }

        }
    }
}
