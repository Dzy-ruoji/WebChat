package com.waxsb.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;
import com.waxsb.model.SocketMsg;
import com.waxsb.model.User;
import com.waxsb.service.Impl.FriendServiceImpl;
import com.waxsb.service.Impl.GroupsServiceImpl;
import com.waxsb.service.Impl.MsgContextServiceImpl;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.WsInfo;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoomController extends ChatRoomServlet {
    public ChatRoomController(){
        super();
    }
    MsgContextServiceImpl msgContextService = MsgContextServiceImplMap.get(session.getId());
    GroupsServiceImpl groupsService = GroupsServiceImplMap.get(session.getId());

    //私聊
    public void privateChat(WsInfo wsInfo){
        msgContextService.insertContext(wsInfo.getSocketMsg());
        //私聊，需要找到发送者和接受者
        String fromUsername = wsInfo.getSocketMsg().getFromUser();//发送者用户名
        String toUsername = wsInfo.getSocketMsg().getToUser();//接收者用户名
        Session fromSession = this.session;
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

    public void publicChat(WsInfo wsInfo){
        //根据群id群发，并插入数据库中
        msgContextService.insertContext(wsInfo.getSocketMsg());
       returnMsg(wsInfo,null,null,4);

    }

    public void returnMsg(WsInfo wsInfo, Session fromSession, Session toSession, int type){
        ObjectMapper mapper = ObjectMapper.get(session.getId());
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
                wsInfo.setResult(getNotReadTotal(wsInfo.getSocketMsg().getToUser()));
                json = MyJson.wbJson(wsInfo);
                toSession.getAsyncRemote().sendText(json);
            }

        } else if(type==3){
            //发送给双方
            json = MyJson.wbJson(wsInfo);
            fromSession.getAsyncRemote().sendText(json);
            if(toSession!=null){
            wsInfo.setMethodName("notReadTotal");
            wsInfo.setResult(getNotReadTotal(wsInfo.getSocketMsg().getToUser()));
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
                    //获取当前群中用户的session,包括发送者自己
                    Session session = UserSession.get(name.getUsername());
                    if (session != null) {//在线
                       json = MyJson.wbJson(wsInfo);
                        //写回客户端，返回的信息通知还没想好
                        session.getAsyncRemote().sendText(json);
                    }
                }
            }
        }
    }

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
            list = new CopyOnWriteArrayList<SocketMsg>();
        }
        list.add(socketMsg);
        out.put(socketMsg.getFromUser(),list);
        messageMap.put(socketMsg.getToUser(),out);
    }

    /** 取出对应好友或群未读消息个数 */
    public Integer getNotReadCount(WsInfo wsInfo){
        String key =wsInfo.getSocketMsg().getType() == 1?wsInfo.getSocketMsg().getToUser():wsInfo.getSocketMsg().getUser_GroupID()+"";
        Map<String, List<SocketMsg>> map = messageMap.get(key);
        return map.get(wsInfo.getSocketMsg().getFromUser())== null?0:map.get(wsInfo.getSocketMsg().getFromUser()).size();
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

//有问题
    public void addFriend(WsInfo wsInfo){
        boolean flag=true;
        FriendServiceImpl friendService = FriendServiceImplMap.get(session.getId());
        SocketMsg socketMsg = wsInfo.getSocketMsg();
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
                returnMsg(wsInfo,fromSession,toSession,2);
            }
        }
    }


}
