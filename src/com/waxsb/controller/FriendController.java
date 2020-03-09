package com.waxsb.controller;

import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;
import com.waxsb.model.User;
import com.waxsb.service.FriendService;
import com.waxsb.service.Impl.FriendServiceImpl;
import com.waxsb.service.Impl.UserServiceImpl;
import com.waxsb.service.UserService;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.ResultInfo;
import com.waxsb.util.Page.PageBean;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/friend/*")
public class FriendController extends BaseServlet {
    private static ResultInfo resInfo=new ResultInfo();
    private static UserService userSer= new UserServiceImpl();
    private static FriendService frSer= new FriendServiceImpl();
    private static ThreadLocal<ResultInfo> resultInfo=new ThreadLocal<ResultInfo>();
    private static ThreadLocal<UserService> userService=new ThreadLocal<UserService>();
    private static ThreadLocal<FriendService> friendService=new ThreadLocal<FriendService>();
    public static ResultInfo getResultInfo() {
        resultInfo.set(resInfo);
        return resultInfo.get();
    }
    public static UserService getUserService() {
        userService.set(userSer);
        return userService.get();
    }
    public static FriendService getFriendService() {
        friendService.set(frSer);
        return friendService.get();
    }

    public void getMyFriend(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("getMyFriend...");
       //获取当前登录用户信息
        User curUser = (User) req.getSession().getAttribute("user");
        System.out.println(curUser);
        //调用service获得好友类
        FriendService friendService = getFriendService();
        ResultInfo resultInfo = getResultInfo();
        UserService userService = getUserService();
        List<User> userList=new ArrayList<User>();

        //获得当前用户的好友类
        List<Friend> friends= friendService.findFriendByName(curUser.getUsername());
        if(friends==null){
            resultInfo = ResultInfo.ResponseSuccess("您暂无好友");
            MyJson.returnJson(resultInfo,resp);
            return;
        }
        resultInfo = ResultInfo.ResponseSuccess(friends);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("getMyFriendSuccess...");
    }

    public void addFriend(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("addFriend...");
        JSONObject jsonObject = MyJson.getJson(req);
        //获得当前用户信息
        User user = (User) req.getSession().getAttribute("user");
        //获得当前用户的好友类
        List<User> userList = (List<User>) req.getSession().getAttribute("userList");

        FriendService friendService = getFriendService();
        ResultInfo resultInfo = getResultInfo();
        AddFriend addFriend=new AddFriend();
        if(jsonObject==null){
            System.out.println("没获得前端传回的数据");
            return;
        }else{
            String username = (String) jsonObject.get("username");
            //要添加的用户
            if(userList==null){
                System.out.println("您暂无好友");
            }else{
                for(User myFriends:userList){
                    if(myFriends.getUsername().equals(username)){
                        System.out.println("已经是好友了，请勿重复添加");
                        return;
                    }
                 }
            }
            //到这里说明不是好友（可能之前发送过请求，不过对方没同意，Friend表中没）
            addFriend.setFriend_1(user.getUsername());
            addFriend.setFriend_2(username);
            addFriend.setF1_allow(AddFriend.getALLOW());
            //因为是发送添加好友请求，所以接收请求方F2_allow默认为空
            //将数据插入addFriend表
            friendService.addFriend(addFriend);
            //将数据返回给前端
           resultInfo = ResultInfo.ResponseSuccess("请求已经发出，请耐心等待");
           MyJson.returnJson(resultInfo,resp);
        }
    }

    public void findFriendRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("findFriendRequest...");
        //获取当前登录的用户
        User user = (User) req.getSession().getAttribute("user");
        FriendService friendService = getFriendService();
        ResultInfo resultInfo = getResultInfo();
        UserService userService = getUserService();
        //返回了需要当前用户需要处理的AddFriend类（处理请求）
        List<AddFriend> friendRequestList=friendService.findFriendRequest(user.getUsername());
        List<User> userList= new ArrayList<User>();
        //将发送请求的用户信息储存到userList中
        if(friendRequestList==null){
            System.out.println("无请求");
        }else{
            for(AddFriend friend:friendRequestList){
                User userRequest=userService.findUserByUsername(friend.getFriend_1());
                System.out.println(userRequest.getUsername());
                userList.add(userRequest);
            }
        }
        resultInfo = ResultInfo.ResponseSuccess(userList);
        System.out.println("requestsuccess");
        //将UserList返回给前端
        MyJson.returnJson(resultInfo,resp);
    }

    public void findRespMessageList(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("user");
        String username = user.getUsername();
        FriendService friendService = getFriendService();
        ResultInfo resultInfo = getResultInfo();
        //返回了添加朋友后其他人的请求（ALLOW同意、DISALLOW拒绝）
        List<AddFriend> respMessageList = friendService.responseMessage(user.getUsername());
        resultInfo = ResultInfo.ResponseSuccess(respMessageList);
        MyJson.returnJson(resultInfo,resp);
    }

    public void findFriendResponse(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("findFriendResp...");
        //获取当前登录的用户
        User user = (User) req.getSession().getAttribute("user");
        FriendService friendService = getFriendService();
        ResultInfo resultInfo = getResultInfo();
        UserService userService = getUserService();

        //返回了添加朋友后其他人的请求（ALLOW同意、DISALLOW拒绝）
        List<AddFriend> respMessageList = friendService.responseMessage(user.getUsername());
        resultInfo = ResultInfo.ResponseSuccess("这是respMessageList",respMessageList);
        MyJson.returnJson(resultInfo,resp);
        //将respMessageList返回给前端
        System.out.println("respMessagesuccess");
    }

    public void agreeReq(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("agreeResp...");
        JSONObject jsonObject = MyJson.getJson(req);
        ResultInfo resultInfo = getResultInfo();
        FriendService friendService = getFriendService();
        //前端传来的用户信息 friend_1表示发送请求的用户 当前用户表示接收请求
        User user = (User) req.getSession().getAttribute("user");
        String friend_1 = (String) jsonObject.get("friend_1");
        String friend_2 = user.getUsername();
        Friend friend=new Friend();
        AddFriend addFriend = new AddFriend();
        addFriend.setFriend_1(friend_1);
        addFriend.setFriend_2(friend_2);
        addFriend.setF2_allow(AddFriend.ALLOW);
        //将当前用户的姓名和好友储存到friend类中
        friend.setFriend_1(friend_1);
        friend.setFriend_2(friend_2);
        //改变addFriend表中的F2_allow
        friendService.friendReqResp(addFriend);
        //确认好友关系，修改friend表
        friendService.allowFriendReq(friend);
        resultInfo = ResultInfo.ResponseSuccess("添加成功");
        MyJson.returnJson(resultInfo,resp);
        System.out.println("agreeResp Success");
    }

    public void rejectReq(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("rejectReq...");
        JSONObject jsonObject = MyJson.getJson(req);
        ResultInfo resultInfo = getResultInfo();
        FriendService friendService = getFriendService();
        //前端传来的用户信息 friend_1表示发送请求的用户 当前用户表示接收请求
        User user = (User) req.getSession().getAttribute("user");
        String friend_1 = (String) jsonObject.get("friend_1");
        String friend_2 = user.getUsername();
        Friend friend=new Friend();
        AddFriend addFriend = new AddFriend();
        addFriend.setFriend_1(friend_1);
        addFriend.setFriend_2(friend_2);
        addFriend.setF2_allow(AddFriend.DISALLOW);
          //将当前用户的姓名和好友储存到friend类中
        friendService.friendReqResp(addFriend);
        //改变addFriend表中的F2_allow
        resultInfo = ResultInfo.ResponseSuccess("您已拒绝");
        MyJson.returnJson(resultInfo,resp);
        System.out.println("agreeResp Success");

    }

    public void deleteReqRespMsg(HttpServletRequest req,HttpServletResponse resp){
        System.out.println("deleteReqResp...");
        //主动发出邀请后的用户得到回答后才有资格删掉
        User user = (User) req.getSession().getAttribute("user");
        String friend_1 = user.getUsername();
        FriendService friendService = getFriendService();
        friendService.deleteMsg(friend_1);

    }

    public void deleteMyFriend(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("deleteMyFriend");
        User user = (User) req.getSession().getAttribute("user");
        String myName = user.getUsername();
        FriendService friendService = getFriendService();
        ResultInfo resultInfo = getResultInfo();
        //从前端获取要删除好友的名称
        JSONObject jsonObject = MyJson.getJson(req);
        String friendName = (String) jsonObject.get("friendName");
        friendService.deleteMyFriend(myName,friendName);
        resultInfo = ResultInfo.ResponseSuccess("删除成功");
        MyJson.returnJson(resultInfo,resp);
        System.out.println("deleteSuccess");
    }

    public void addNickname(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("addNickname");
        //给好友设置昵称
        User user = (User) req.getSession().getAttribute("user");
        String MyName = user.getUsername();
        //获取好友用户名、要设置的昵称
        JSONObject jsonObject = MyJson.getJson(req);
        ResultInfo resultInfo = getResultInfo();
        String friendName = (String) jsonObject.get("friendName");
        String nickname = (String) jsonObject.get("nickname");
        FriendService friendService = getFriendService();
        //修改好友昵称
        friendService.updateNickname(MyName,friendName,nickname);
        resultInfo = ResultInfo.ResponseSuccess("修改成功");
        MyJson.returnJson(resultInfo,resp);
        System.out.println("addNicknameSuccess");
    }

    public void showNickname(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("showNickname");
        //获取当前登录用户信息
        User curUser = (User) req.getSession().getAttribute("user");
        String MyName = curUser.getUsername();
        //调用service获得好友类
        FriendService friendService = getFriendService();
        ResultInfo resultInfo = getResultInfo();
        UserService userService = getUserService();
        List<User> userList=new ArrayList<User>();
        //获得当前用户的好友类
        List<Friend> friends= friendService.findFriendByName(MyName);
        List<Friend> friendsNameAndNickName = null;
        if(friends==null){
            resultInfo = ResultInfo.ResponseSuccess("您暂无好友");
            MyJson.returnJson(resultInfo,resp);
            return;
        }
        for(Friend friend:friends){
            Friend MyFriend = new Friend();
            if(friend.getFriend_1().equals(MyName)){
                //friend_2是我的好友，
                MyFriend.setFriend_2(friend.getFriend_2());
                MyFriend.setFriendNickname_2(friend.getFriendNickname_2());
            }else{
                //friend_1是我的好友
                MyFriend.setFriend_1(friend.getFriend_1());
                MyFriend.setFriendNickname_1(friend.getFriendNickname_1());
            }
            //把好友类放在list集合中
            friendsNameAndNickName.add(MyFriend);
        }
        //将当前用户的好友集合储存到session中
        req.getSession().setAttribute("friendsNameAndNickName",friendsNameAndNickName);
        //必有两个属性为null
        resultInfo = ResultInfo.ResponseSuccess(friendsNameAndNickName);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("showNicknameSuccess...");
    }

    
}
//好友模块
//一：获取我的好友
//1.获取当前登录用户 req.getSession().getAttribute("user");
//2.获取当前登录的用户名
//3.根据用户名查询数据库中的好友表，获得好友类（当前用户、当前用户的好友、friend_1、friend_2）的List集合
//4.遍历好友类List集合，如果friend_1是当前用户，根据friend_2的名字查询数据库获得User类，如果friend_1不是当前用户，则根据friend_1的名字查询数据库获得User类
//5.将User类一个个的放在新建的User List集合中（就是一个用户的好友全都放进去）

//二：添加好友
//1.获取前端传过来的要添加的好友名称/id  （这里统一用名称吧）
//2.遍历当前用户的User List集合，判断是否已经是好友
//3.如果不是好友，调用Service层添加好友 Service.addFriend(friend)
//4.返回数据给前端

//三：“查看”好友请求及请求响应（是同意还是拒绝）
//1.获取当前登录用户 req.getSession().getAttribute("user");
//2.获取当前登录用户名称
//3.根据当前登录用户名称查询数据库中的AddFriend类返回AddFriend类的List集合（返回了好多个关于这个用户的请求）
//4.创建一个User类的List 遍历AddFriend类的List集合，根据一个个AddFriend类中的Friend_1（发送请求的用户）的名字返回一个个User并存入先前创建的User List集合中
//5.遍历User List集合中的用户
//(第四步的目的是返回前端显示)
//6.根据当前用户名称返回AddFriend类的List集合
//.返回数据

//四：同意或拒绝好友请求
//4.1 同意好友请求
//1.获取前端传回的addFriend类
//2. new 一个Friend类，将addFriend类中的信息储存到Friend中（Friend_1 Friend_2）
//3.调用service层将Friend储存到数据库中
//4.返回数据

//4.2 拒绝好友请求
//1.获取数据
//2.设置addFriend类 （主要是给另一方回应拒绝消息）
//3.返回信息

