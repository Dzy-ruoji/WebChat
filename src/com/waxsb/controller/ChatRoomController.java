package com.waxsb.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waxsb.model.*;
import com.waxsb.service.Impl.FriendServiceImpl;
import com.waxsb.service.Impl.GroupsServiceImpl;
import com.waxsb.service.Impl.MsgContextServiceImpl;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.WsInfo;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoomController extends ChatRoomServlet {
    public ChatRoomController(){
        super();
    }

    //私聊
    public void privateChat(WsInfo wsInfo,Session session){
        //私聊，需要找到发送者和接受者
        String fromUsername = wsInfo.getSocketMsg().getFromUser();//发送者用户名
        String toUsername = wsInfo.getSocketMsg().getToUser();//接收者用户名
        Session fromSession = session;
        //通过接收者用户名寻找session
        Session toSession = UserSession.get(toUsername);
        if(toSession!=null){
            //发送给接受者和接收者 返回对象
           returnMsg(wsInfo,fromSession,toSession,3);
        }else {
            //发给自己
            returnMsg(wsInfo,fromSession,toSession,1);
        }
    }

    public void publicChat(WsInfo wsInfo,Session session){
       returnMsg(wsInfo,session,null,4);
    }

    //传回消息的同时顺便存到Map集合中
    public void returnMsg(WsInfo wsInfo, Session fromSession, Session toSession, int type){
        saveMsg(wsInfo.getSocketMsg());
        ObjectMapper mapper = ObjectMapper.get(fromSession.getId());
        GroupsServiceImpl groupsService = GroupsServiceImplMap.get(fromSession.getId());
        MsgContextServiceImpl msgContextService = MsgContextServiceImplMap.get(fromSession.getId());
        msgContextService.insertContext(wsInfo.getSocketMsg());
        String json =null;
        //将json数据写回客户端
        if(type==1){
            //返回给自己
            json = MyJson.wbJson(wsInfo);
            fromSession.getAsyncRemote().sendText(json);
        }else if(type==2){
            //发送给对方
            if(toSession!=null){
                wsInfo.setMethodName("notReadTotal");
                wsInfo.setResult(getNotReadTotal(wsInfo,session));
                json = MyJson.wbJson(wsInfo);
                toSession.getAsyncRemote().sendText(json);
            }

        } else if(type==3){
            //发送给双方
            json = MyJson.wbJson(wsInfo);
            fromSession.getAsyncRemote().sendText(json);
            if(toSession!=null){
            wsInfo.setMethodName("notReadTotal");
            wsInfo.setResult(getNotReadTotal(wsInfo,session));
            json = MyJson.wbJson(wsInfo);
            toSession.getAsyncRemote().sendText(json);
            }
        }else if(type==4){
            //返回群
            //根据群id获取所有用户名信息
            //发一条信息就要查询一次数据库 优化（查询一个数据库后存到map集合中，等到有人退出或者加入时谁同意谁更新map集合）
            List<User> memberName = groupsService.memberName(wsInfo.getSocketMsg().getUser_GroupID());
            if(memberName!=null){

                //根据用户名获得sessionid发送
                for(User name : memberName) {
                    //获取当前群中用户的session,包括发送者自己,当前无法获取session
                    toSession = UserSession.get(name.getUsername());
                    if (toSession != null) {//在线
                       json = MyJson.wbJson(wsInfo);
                        //写回客户端，返回的信息通知还没想好
                        toSession.getAsyncRemote().sendText(json);
                    }
                }
            }
        }
    }

    public void saveMsg(SocketMsg socketMsg){
        //用户不在线，将消息存入表中并且存入map集合中
        //key是接收方的用户名，value是待接收的list集合
        //map :  key(接收者) value(map(key:发送者:value(是这个好友发送给该接收者的所有消息)))
        //如果好友名字和群id一样会出现bug
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
            list = new CopyOnWriteArrayList<SocketMsg>();
        }
        list.add(socketMsg);
        out.put(socketMsg.getFromUser(),list);
        messageMap.put(socketMsg.getToUser(),out);
    }

    /** 取出对应好友或群未读消息个数 */
    public void getNotReadCount(WsInfo wsInfo,Session session){//不能获得当前用户已读的消息记录，有bug可能在执行这个方法之前执行了remove方法

        Integer count=0;
        String key =wsInfo.getSocketMsg().getType() == 1?wsInfo.getSocketMsg().getToUser():wsInfo.getSocketMsg().getUser_GroupID()+"";
        if(messageMap.get(key)!=null){
            Map<String, List<SocketMsg>> map = messageMap.get(key);
             count = map.get(wsInfo.getSocketMsg().getFromUser())== null?0:map.get(wsInfo.getSocketMsg().getFromUser()).size();
        }
        wsInfo.setMethodName("getNotReadCount");
        wsInfo.setResult(count);
        session.getAsyncRemote().sendText(MyJson.wbJson(wsInfo));
    }

    /** 取所有长度 */
    public Integer getNotReadTotal(WsInfo wsInfo,Session session){
       System.out.println("进来了");
        int total = 0;
        Map<String, List<SocketMsg>> map = messageMap.get(wsInfo.getSocketMsg().getToUser());
        if(map!=null){
            for(String key : map.keySet()){
                total += map.get(key).size();
            }
        }
        if(wsInfo.getSocketMsg().getType()==999){
            //粗略的判断方法，直接访问
            wsInfo.setMethodName("AnotReadTotal");
            wsInfo.setResult(total);
            session.getAsyncRemote().sendText(MyJson.wbJson(wsInfo));
        }
        return total;
    }

    public void removeMap(WsInfo wsInfo,Session session){
        String key1=wsInfo.getSocketMsg().getToUser();
        String key2=null;
        if(wsInfo.getSocketMsg().getUser_GroupID()==-1 && wsInfo.getSocketMsg().getFromUser()!=null){
            //群聊
            key2 = wsInfo.getSocketMsg().getFromUser();
        }else if(wsInfo.getSocketMsg().getUser_GroupID()!=-1 && wsInfo.getSocketMsg().getFromUser()==null){
            //私聊
            key2 = wsInfo.getSocketMsg().getUser_GroupID()+"";
        }
        delete(key1,key2);
    }

    /** 从未读Map中删除已读消息 */
    public void delete(String key1,String key2){//key(接收者) value: key(发送者) value
        //第一次
        if(messageMap.get(key1)!=null){
            messageMap.get(key1).remove(key2);
        }
    }

    //有问题
    public void addFriend(WsInfo wsInfo,Session session){
        boolean flag=true;
        FriendServiceImpl friendService = FriendServiceImplMap.get(session.getId());
        SocketMsg socketMsg = wsInfo.getSocketMsg();
        if(!socketMsg.getToUser().equals(socketMsg.getFromUser())){
            AddFriend addFriend=new AddFriend();
            Session fromSession =session;
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
                            returnMsg(wsInfo,fromSession,toSession,1);
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
                            returnMsg(wsInfo,fromSession,toSession,1);
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
                wsInfo.setMethodName("addFriend");
                returnMsg(wsInfo,fromSession,toSession,2);
            }
        }
    }

    public void  recentContacts(WsInfo wsInfo,Session session){
        System.out.println("recentContacts....");
        MsgContextServiceImpl msgContextService = MsgContextServiceImplMap.get(session.getId());
        GroupsServiceImpl groupsService = GroupsServiceImplMap.get(session.getId());
        FriendServiceImpl friendService = FriendServiceImplMap.get(session.getId());
        //返回最近联系人,没有判断返回的最近联系人长度
        String myName = wsInfo.getSocketMsg().getFromUser();
        List<SocketMsg> soc = msgContextService.findContacts(myName);
        wsInfo.setMethodName("recentContacts");
        for(SocketMsg asoc : soc){
            int count=0;
            if(asoc.getType()==2){//群聊,返回群名称
                String groupId = asoc.getToUser();
                User_Groups group = groupsService.getGroupById(Integer.parseInt(asoc.getToUser()));
                if(group!=null){
                    asoc.setUser_nickname(group.getUG_Name());
                    asoc.setFromUser(group.getUG_Number());
                   //直接返回未读消息数
                    if(messageMap.get(myName)!=null){
                        Map<String, List<SocketMsg>> map = messageMap.get(myName);
                        count = map.get(groupId)== null?0:map.get(wsInfo.getSocketMsg().getFromUser()).size();
                    }
                }
                asoc.setMid(count);

            }else{//私聊，还需返回好友信息
                String friendName = asoc.getToUser();
                String nickname = friendService.findNickname(myName,friendName);
                asoc.setUser_nickname(nickname);
                //直接返回未读消息数
                if(messageMap.get(myName)!=null){
                    Map<String, List<SocketMsg>> map = messageMap.get(myName);
                     count = map.get(friendName)== null?0:map.get(friendName).size();
                }
            }
            asoc.setMid(count);
        }

        wsInfo.setResult(soc);
        session.getAsyncRemote().sendText(MyJson.wbJson(wsInfo));
    }

}
