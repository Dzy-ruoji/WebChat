window.onload=function () {
    FgetMyself();
}

//ajax发送get请求
function GET(url,methodName) {
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET",url+"?"+new Date().getTime(), true);
    //添加HTTP头部
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    //3.发送请求
    xhttp.send();

    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var data = JSON.parse(xhttp.responseText);
            if(methodName=="getMyself"){
                getMyself(data);
            }else if(methodName=="getMyFriends"){
                getMyFriends(data);
            }else if(methodName=="getMyGroups"){
                getMyGroups(data);
            }
        }else {
           // alert("发送GET请求失败");
        }
    }
}

//ajax发送post请求
function POST(url,json,methodName) {
    //发送ajax请求获取数据
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("POST",url+"?"+new Date().getTime(), true);
//添加HTTP头部
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//3.发送请求
    xhttp.send(json);
    xhttp.onreadystatechange = function () {

        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var data = JSON.parse(xhttp.responseText);
            if (methodName == "getData") {
                getData(data);
            }else if(methodName=="getMsgData"){
                getMsgData(data);
            }else if(methodName=="updateNickname"){
                updateNickname(data);
            }else if(methodName=="getGroupData"){
                getGroupData(data);
            }
        }else{
           // alert("发送POST请求失败");
            }
        }
    }

function chat() {
    if ('WebSocket' in window) {
        var wsUrl="ws://localhost:8080/chat/chatRoomSerController/"+myname;
        //客户端与服务端建立连接，建立连接后，会发生一个ws.open事件
        ws = new WebSocket(wsUrl);
        //连接成功后，啥都不干
        ws.onopen=function () {
            //发一条测试消息，获得未读消息
            var socketMsg = {};
            socketMsg.type = 999;
            socketMsg.toUser=myname;
            //可以改进访问后台所有初始化数据methodName改一下
            var Msg = {methodName:"getNotReadTotal",socketMsg:socketMsg}
            ws.send(JSON.stringify(Msg));
        }
        //客户端收到服务器发送的消息,在线时的即时信息，不在线时的多条离线信息,难点
        ws.onmessage = function (message) {
            var WsInfo=JSON.parse(message.data);
            if(WsInfo.methodName=="getNotReadCount"){
                updateNotRead(WsInfo);
            }else if (WsInfo.methodName=="AnotReadTotal") {
                AllNotRead(WsInfo);
            }else if(WsInfo.methodName=="recentContacts"){
                recentContacts(WsInfo);
            }else if(WsInfo.methodName=="addFriend"){
                alert(WsInfo.result);
            }
            else{
                //展示当前聊天好友和实时发送的信息
                OurMessage(WsInfo);
                //展示未读消息总数
                AllNotRead(WsInfo);
                //刷新最新的消息
                if(document.getElementById("mesID").className=="selected"){
                    FnewMsg();
                }
            }
        }
        ws.onerror = function () {
            alert("WebSocket连接发生未知错误");
        }
     }else {
        alert('当前浏览器不支持WebSocket');
    }
}

//展示当前聊天好友和实时发送的信息
function OurMessage(WsInfo) {//message为后台传回的信息，connectNow是当前正在聊天的人
    var msgList = WsInfo.socketMsg;
    var flag = false;
    var connectNow = document.getElementById("connectNameDiv").getAttribute("value");
    if(msgList.type==1){
        if(connectNow==msgList.toUser||connectNow==msgList.fromUser){
            flag = true;
        }else {
            flag = false;
        }
    }else{
        if(connectNow==msgList.user_GroupID){
            flag = true;
        }else{
            flag = false;
        }
    }
    if(flag) {
        var Msg = msgList;
        var div = document.createElement("div");
        var ourMessage = document.getElementById("ourMessageDiv");
        ourMessage.append(div);
        var span = document.createElement('span');
        div.append(span);
        var p = document.createElement('p');
        div.append(p);
        p.innerText = Msg.msg;
        if (Msg.fromUser == myname) {
            //是本人发的
            div.setAttribute("class", "me");
            span.setAttribute("class", "meName");
            span.innerText = Msg.fromUser;
        } else {
            //不是本人发的
            div.setAttribute("class", "other");
            span.setAttribute("class", "itsName");
            span.innerText = Msg.fromUser;
        }

    }
    var msg = document.getElementById("ourMessageDiv");
    //滚动条
    Scroll(msg);
}

//滚动条
function Scroll(msg) {
    //滚动条
    //差值 溢出的高度减掉可视高度
    var distance = msg.scrollHeight-msg.offsetHeight;
    //将差值赋给滚动条的高度
    msg.scrollTop = distance;
}

//未读消息总数展示（未完成移除样式）
function AllNotRead(WsInfo) {
    var connectNow = document.getElementById("connectNameDiv").getAttribute("value");
    var notReadTotal = WsInfo.result;
    var user_GroupID = WsInfo.socketMsg.user_GroupID;
    var fromUser = WsInfo.socketMsg.fromUser;
    var type;
    if ( connectNow == fromUser) {
        type = 1;
        notReadTotal = notReadTotal - 1;
        haveRead(type,myname,connectNow);
    }else if(connectNow == user_GroupID){
        type = 2;
        notReadTotal = notReadTotal - 1;
        haveRead(type,myname,connectNow);
    }

    if(notReadTotal>0){
        //执行读取方法
        document.getElementById("manySpan").setAttribute("class","many");
        document.getElementById("manySpan").innerText=notReadTotal;
    }else{
        //移除样式
        document.getElementById("manySpan").innerText=" ";
        document.getElementById("manySpan").removeAttribute("many");
    }
}

//某个群组或好友未读消息
function NotRead(type,key1,key2) {
    //key1是接受者 key2是发送者
    var socketMsg = {};
    if(type==1){
        //读取私聊信息
        socketMsg.type = 1;
        socketMsg.user_GroupID=-1;
        socketMsg.toUser = myname;
        socketMsg.fromUser = key2;
    }else if(type==2){
        socketMsg.type = 2;
        socketMsg.toUser = myname;
        socketMsg.user_GroupID=key2;
    }
    var Msg = {methodName:"getNotReadCount",socketMsg:socketMsg}
    ws.send(JSON.stringify(Msg));
}

function updateNotRead(WsInfo) {
   var AllCount = document.getElementById("manySpan").innerText;
   var count = WsInfo.result;
   var c = (+AllCount) - count;

   if(c>0){
       document.getElementById("manySpan").innerText = c;
   }else{
       document.getElementById("manySpan").innerText = "";
       document.getElementById("manySpan").removeAttribute("many");
   }
}

//发送信息给后台
function getMessage() {
    var toUser=null;
    var user_GroupID=null;
    var type = document.getElementById("Type").value;
    var inputMessage = document.getElementById("inputMessage").value;
    var fromUser = myname;
    //暂且用户只能发送纯文本，以后再改
    var msgType = 1;
    var methodName;
    if(type==1){
        //私聊
        toUser = document.getElementById("connectNameDiv").innerText;
        methodName = "privateChat";
    }else{
        user_GroupID = +document.getElementById("connectNameDiv").getAttribute("value");
        methodName = "publicChat";
    }
    //根据id获取要发送的对象
    // var Msg = {msg:inputMessage,toUser:toUser,fromUser:fromUser,type:type,msgType:msgType,user_GroupID:user_GroupID};
    var socketMsg = {};
    socketMsg.user_GroupID=user_GroupID;
    socketMsg.msg = inputMessage;
    socketMsg.toUser = toUser;
    socketMsg.fromUser = fromUser;
    socketMsg.type = type;
    socketMsg.msgType = msgType;
    var Msg = {methodName:methodName,socketMsg:socketMsg}

    //获取消息后，将消息发送给服务端,暂时会造成消息泄露
    if(typeof (inputMessage) == 'undfined'||inputMessage ==""){
        alert("请输入您要发送的消息");
    }else {
        document.getElementById("inputMessage").value=" ";
        //这个是关键
        ws.send(JSON.stringify(Msg));
        //清空文本框
    }
}

//按回车发送消息
document.onkeyup = function (e) {
    if(e.keyCode ==13){
        getMessage();
    }
}

//关闭页面或者用户退出时，会执行一个ws.close方法
window.onbeforeunload = function () {
    ws.close();
}

//获取自身消息 myname为全局变量
function FgetMyself() {
    GET("/user/getMyself","getMyself");
}

function getMyself(data) {
    var user = data.data;
    if (user != null) {
        myname = user.username;
        chat();
        FgetMyFriends();


    } else {
        alert("您尚未登录");
    }

}

//获取好友列表
function FgetMyFriends() {
    document.getElementById('connectsID').setAttribute("class","selected");
    document.getElementById('mesID').classList.remove('selected');
    document.getElementById('groupsID').classList.remove('selected');
    //动态创建好友列表,先清空再创建
    var LiArray = document.getElementById('messageListUl').getElementsByTagName('li');
    var LiLen = LiArray.length;
    for (var i = 0; i < LiLen; i++) {
        LiArray[0].remove();
    }
    GET("/friend/getMyFriend","getMyFriends");

}

function getMyFriends(data) {
    //拿到了好友类集合
    var myFriendList = data.data;
    if(myFriendList.length>0) {
        for (var i = 0; i < myFriendList.length; i++) {
            //展示好友信息
            var myFriend = myFriendList[i];
            var li = document.createElement("li");
            li.setAttribute("class", "Contact Template");
            var messageList = document.getElementById("messageListUl");
            messageList.append(li);
            if (myFriend.friend_1 == myname) {
                //说明myFriend.friend_2是好友姓名，myFriend.friendNickname_2是我给好友取得昵称
                //还需要显示昵称,改了之后会有问题
                if (myFriend.friendNickname_2 != null) {
                    var show = myFriend.friend_2 + "(" + myFriend.friendNickname_2 + ")";
                    li.innerHTML = show + " <button>删除</button> <button onclick='updateNickname(this)' value=" + (myFriend.friend_2) + ">修改</button> <button onclick='private(this)' value=" + (myFriend.friend_2) + ">聊天</button>"
                } else {
                    li.innerHTML = myFriend.friend_2 + "<button>删除</button> <button onclick='updateNickname(this)' value=" + (myFriend.friend_2) + ">修改</button> <button onclick='private(this)'  value=" + (myFriend.friend_2) + ">聊天</button>"
                }
            } else {
                if (myFriend.friendNickname_1 != null) {
                    var show = myFriend.friend_1 + "(" + myFriend.friendNickname_1 + ")";
                    li.innerHTML = show + "<button>删除</button> <button onclick='updateNickname(this)'  value=" + (myFriend.friend_1) + ">修改</button> <button onclick='private(this)' value=" + (myFriend.friend_1) + ">聊天</button>"
                } else {
                    li.innerHTML = myFriend.friend_1 + "<button>删除</button> <button onclick='updateNickname(this)'  value=" + (myFriend.friend_1) + ">修改</button> <button onclick='private(this)' value=" + (myFriend.friend_1) + ">聊天</button>"
                }
            }
        }
    }

}

//获取群列表，动态创建群组列表
function FgetMyGroups() {
    document.getElementById('groupsID').setAttribute("class","selected");
    document.getElementById('mesID').classList.remove('selected');
    document.getElementById('connectsID').classList.remove('selected');
    //动态创建好友列表,先清空再创建
    var LiArray = document.getElementById('messageListUl').getElementsByTagName('li');
    var LiLen = LiArray.length;
    for (var i = 0; i < LiLen; i++) {
        LiArray[0].remove();
    }
    GET("/groupChat/getMyGroups","getMyGroups");
}

function getMyGroups(data) {
    var myGroupsList = data.data.user_groupsList;
    if (myGroupsList.length > 0) {
        for (var i = 0; i < myGroupsList.length; i++) {
            //展示群组信息
            var myGroups = myGroupsList[i];
            var li = document.createElement("li");
            li.setAttribute("class", "Contact Template");
            var messageList = document.getElementById("messageListUl");
            messageList.append(li);
            li.innerHTML = myGroups.user_Groups.ug_Name +"(" + myGroups.user_Groups.ug_Number +")"+ "<button>退出</button> <button  onclick='public(this)' title=" + myGroups.user_Groups.ug_Name + " ''  value=" + myGroups.user_Groups.ug_ID + ">聊天</button>"
        }
    }
}

//groupname为查询的全局变量
document.getElementById("searchgroup").onclick=function () {
    groupname=document.getElementById("groupname").value;

    if(groupname!=''){
        FgetGroupData(1,5,groupname);
    }else{
        FgetGroupData(1,5);
    }
    return false;
};


//friendName为全局变量(查询的名字)
function searchMyFriend() {
    var pageBean;
    document.getElementById("test").style.display = 'block';
    friendName = document.getElementById("searchName").value;
    if(friendName!=""&&friendName.length>1){
        FgetData(1,5,friendName)
    }else {
        FgetData(1, 5);
        document.getElementById("search").onclick=function () {
            var name = document.getElementById("username").value;
            if(name!=''){
                friendname = name;
                FgetData(1, 5,friendname);
            }
            return false;
        };
    }
}

function FgetGroupData(pageNum,pageSize,groupname) {
    var jsonObj=new Object();
    jsonObj.currentPage=pageNum;
    jsonObj.row=pageSize;
    if(groupname!="undefined"){
        jsonObj.groupname=groupname;
    }
    var json= JSON.stringify(jsonObj);
    var UiArray = document.getElementById("dataTable").getElementsByTagName('ul');
    var UiLen = UiArray.length;
    for (var i = 0; i < UiLen; i++) {
        //干掉所有ul
        UiArray[0].remove();
    }
    var DivArray = document.getElementById("read").getElementsByTagName("div");
    var DivLen = DivArray.length;
    for (var i = 0; i < DivLen; i++) {
        if (i != 0) {
            DivArray[1].remove();
        }
    }

    //清空分页条里的页码
    var AArrary = document.getElementById("pageBox").getElementsByTagName("a");
    var ALen = AArrary.length;
    for (var i = 0; i <= ALen; i++) {
        if (i != 0) {
            AArrary[0].remove();
        }
    }
    POST("/groupChat/findGroupByNameOrNum",json,"getGroupData");
}

function getGroupData(data) {
    pageBean=data.data;
    //------------------>展示数据
    //获取该页对应数据
    var groupList = pageBean.list;
    //userList -->  [{},{},{}]
    var index = 0;
    var  LiLen=5;
    var ul = document.createElement("ul");
    ul.setAttribute("class", "clearfix");
    var dataTable = document.getElementById("dataTable");
    dataTable.append(ul);
    var li = [];
    for (var j = 0; j < LiLen; j++) {
        li[j] = document.createElement("li");
        li[j].style.overflow = "hidden";
        ul.appendChild(li[j]);
    }
    for (var j = 0; j < LiLen;) {
        li[j++].innerHTML = "编号" ;
        li[j++].innerHTML = "群号";
        li[j++].innerHTML = "群名" ;
        li[j++].innerHTML = "创建日期" ;
        li[j++].innerHTML = "操作";
    }

    var div = document.createElement("div");
    div.setAttribute("class","bottom");
    var read = document.getElementById("read");
    read.append(div);

    for (var i = 0; i < groupList.length; i++) {
        //当前要展示的用户信息
        group = groupList[i];
        var ul = document.createElement("ul");
        ul.setAttribute("class", "message clearfix");
        /* var dataTable = document.getElementById("dataTable");
         dataTable.append(ul);*/
        div.append(ul);
        var li = [];
        for (var j = 0; j < LiLen; j++) {
            li[j] = document.createElement("li");
            li[j].style.overflow = "hidden";
            ul.appendChild(li[j]);
        }

        for (var j = 0; j < LiLen;) {
             li[j++].innerHTML = "" + ((++index)+(pageBean.currentPage-1)*pageBean.rows);
            li[j++].innerHTML = "" + group.ug_Number;
            li[j++].innerHTML = "" + group.ug_Name;
            var newTime = new Date(group.ug_CreateTime);
            li[j++].innerHTML = "" + newTime.toLocaleDateString();
            //这里的myname应该在getMyself中定义了全局变量
            li[j++].innerHTML = "<button title ='"+(group.UG_ID)+"'  onclick=\"addGroup(this);\">添加群</button>";
        }
    }

    //-------------------->展示分页条
    var style;
    var pageBox = document.getElementById("pageBox");
    //上一页
    var prevPage = document.createElement("a");
    prevPage.setAttribute("href", "javascript:;");
    prevPage.setAttribute("id", "prevPage");
    prevPage.setAttribute("title", pageBean.prevPage);
    prevPage.setAttribute("class", "page");
    prevPage.innerHTML = "<"
    if (pageBean.isFirst) {
        style = document.createElement("style");
        prevPage.setAttribute("class", "page not");
        prevPage.style.color = "gray";
    } else {
        prevPage.onclick = function () {
            if(groupname!=""&&groupname.length>1){
                FgetGroupData(pageBean.currentPage -1, 5,groupname);
            }else{
                FgetGroupData(pageBean.currentPage - 1,5);
            }
        }
    }
    pageBox.append(prevPage);

    //当前页
    for (var i = 0; i < pageBean.totalPage; i++) {
        var page = document.createElement("a");
        page.setAttribute("href", "javascript:;");
        page.setAttribute("class", "page");
        page.innerHTML = " " + (i + 1);
        //将页码添加
        if (i + 1 == pageBean.currentPage) {
            style = document.createElement("style");
            page.setAttribute("class", "page not");
            page.style.color = "gray";
        } else {
            page.onclick = function () {
                if(groupname!=""&&groupname.length>1){
                    FgetGroupData(+this.innerHTML, 5,groupname);
                }else{
                    FgetGroupData(+this.innerHTML,5);
                }
            }
        }
        pageBox.append(page);
    }


    //下一页
    var nextPage = document.createElement("a");
    nextPage.setAttribute("href", "javascript:;");
    nextPage.setAttribute("id", "prevPage");
    nextPage.setAttribute("title", pageBean.nextPage);
    nextPage.setAttribute("class", "page");
    nextPage.innerHTML = ">"
    if (pageBean.isLast) {
        style = document.createElement("style");
        nextPage.setAttribute("class", "page not");
        nextPage.style.color = "gray";
    } else {
        nextPage.onclick = function () {
            if(groupname!=""&&groupname.length>1){
                FgetGroupData(pageBean.currentPage + 1, 5,groupname);
            }else{
                FgetGroupData(pageBean.currentPage + 1, 5);
            }
        }
    }
    pageBox.append(nextPage);
    document.getElementById("totalCount").innerText = pageBean.totalCount;
    document.getElementById("totalPage").innerText =pageBean.totalPage;

}

//获取数据(根据传递过来的页码，来获取该页数据，并展示在页面上，并生成对应的分页条)
function FgetData(pageNum, pageSize,friendname) {
    var jsonObj=new Object();
    jsonObj.currentPage=pageNum;
    jsonObj.row=pageSize;
    if(friendname!="undefined"){
        jsonObj.username=friendname;
    }

    var json= JSON.stringify(jsonObj);
    //干掉除第一个div(dataTable)所有ul
    var UiArray = document.getElementById("dataTable").getElementsByTagName('ul');
    var UiLen = UiArray.length;
    for (var i = 0; i < UiLen; i++) {
       /* if (i != 0) {
            UiArray[1].remove();
        }*/
       //干掉所有ul
       UiArray[0].remove();
    }

    var DivArray = document.getElementById("read").getElementsByTagName("div");
    var DivLen = DivArray.length;
    for (var i = 0; i < DivLen; i++) {
         if (i != 0) {
             DivArray[1].remove();
         }
    }

    //清空分页条里的页码
    var AArrary = document.getElementById("pageBox").getElementsByTagName("a");
    var ALen = AArrary.length;
    for (var i = 0; i <= ALen; i++) {
        if (i != 0) {
            AArrary[0].remove();
        }
    }
    POST("/user/findUserBySearchName",json,"getData");
}

function getData(data) {
    //表示分页对象
    pageBean = data.data;
    //------------------>展示数据
    //获取该页对应数据
    var userList = pageBean.list;
    //userList -->  [{},{},{}]
    var index = 0;
   // var LiLen = document.getElementById("lif").getElementsByTagName("li").length;
    var  LiLen=8;
    var ul = document.createElement("ul");
    ul.setAttribute("class", "clearfix");
    var dataTable = document.getElementById("dataTable");
    dataTable.append(ul);
    var li = [];
    for (var j = 0; j < LiLen; j++) {
        li[j] = document.createElement("li");
        li[j].style.overflow = "hidden";
        ul.appendChild(li[j]);
    }
    for (var j = 0; j < LiLen;) {
      /*  li[j++].innerHTML = "<label><input value=\"2\" class=\"a-checkbox\" type=\"checkbox\"><div class=\"b-checkbox\"></div></label>"
     */   li[j++].innerHTML = "编号" ;
        li[j++].innerHTML = "用户名";
        li[j++].innerHTML = "姓名" ;
        li[j++].innerHTML = "性别" ;
        li[j++].innerHTML = "出生日期" ;
        li[j++].innerHTML = "手机号" ;
        li[j++].innerHTML = "邮箱";
        li[j++].innerHTML = "操作";
    }

    var div = document.createElement("div");
    div.setAttribute("class","bottom");
    var read = document.getElementById("read");
    read.append(div);

    for (var i = 0; i < userList.length; i++) {
        //当前要展示的用户信息
        user = userList[i];
        var ul = document.createElement("ul");
        ul.setAttribute("class", "message clearfix");
       /* var dataTable = document.getElementById("dataTable");
        dataTable.append(ul);*/
        div.append(ul);
        var li = [];
        for (var j = 0; j < LiLen; j++) {
            li[j] = document.createElement("li");
            li[j].style.overflow = "hidden";
            ul.appendChild(li[j]);
        }

        for (var j = 0; j < LiLen;) {
          /*  li[j++].innerHTML = "<label><input value=\"2\" class=\"a-checkbox\" type=\"checkbox\"><div class=\"b-checkbox\"></div></label>"
         */   li[j++].innerHTML = "" + ((++index)+(pageBean.currentPage-1)*pageBean.rows);
            li[j++].innerHTML = "" + user.username;
            li[j++].innerHTML = "" + user.name;
            li[j++].innerHTML = "" + user.gender;
            li[j++].innerHTML = "" + user.birthday;
            li[j++].innerHTML = "" + user.telephone;
            var x = j++;
            var email =user.email;
            if(email.length>14){
                email = user.email.substring(0,11)+"...";
            }
            li[x].innerHTML = "" + email;
            li[x].title = user.email;
            //这里的myname应该在getMyself中定义了全局变量
            li[j++].innerHTML = "<button title ='"+(user.username)+"'  onclick=\"addFriend(this);\">添加好友</button>";
        }
    }

    //-------------------->展示分页条
    var style;
    var pageBox = document.getElementById("pageBox");
    //上一页
    var prevPage = document.createElement("a");
    prevPage.setAttribute("href", "javascript:;");
    prevPage.setAttribute("id", "prevPage");
    prevPage.setAttribute("title", pageBean.prevPage);
    prevPage.setAttribute("class", "page");
    prevPage.innerHTML = "<"
    if (pageBean.isFirst) {
        style = document.createElement("style");
        prevPage.setAttribute("class", "page not");
        prevPage.style.color = "gray";
    } else {
        prevPage.onclick = function () {
            if(friendName!=""&&friendName.length>1){
                FgetData(pageBean.currentPage -1, 5,friendname);
            }else{
                FgetData(pageBean.currentPage - 1,5);
            }
        }
    }
    pageBox.append(prevPage);

    //当前页
    for (var i = 0; i < pageBean.totalPage; i++) {
        var page = document.createElement("a");
        page.setAttribute("href", "javascript:;");
        page.setAttribute("class", "page");
        page.innerHTML = " " + (i + 1);
        //将页码添加
        if (i + 1 == pageBean.currentPage) {
            style = document.createElement("style");
            page.setAttribute("class", "page not");
            page.style.color = "gray";
        } else {
            page.onclick = function () {
                if(friendName!=""&&friendName.length>1){
                    FgetData(+this.innerHTML, 5,friendname);
                }else{
                    FgetData(+this.innerHTML,5);
                }
            }
        }
        pageBox.append(page);
    }


    //下一页
    var nextPage = document.createElement("a");
    nextPage.setAttribute("href", "javascript:;");
    nextPage.setAttribute("id", "prevPage");
    nextPage.setAttribute("title", pageBean.nextPage);
    nextPage.setAttribute("class", "page");
    nextPage.innerHTML = ">"
    if (pageBean.isLast) {
        style = document.createElement("style");
        nextPage.setAttribute("class", "page not");
        nextPage.style.color = "gray";
    } else {
        nextPage.onclick = function () {
            if(friendName!=""&&friendName.length>1){
                FgetData(pageBean.currentPage + 1, 5,friendname);
            }else{
                FgetData(pageBean.currentPage + 1, 5);
            }
        }
    }
    pageBox.append(nextPage);
    document.getElementById("totalCount").innerText = pageBean.totalCount;
    document.getElementById("totalPage").innerText =pageBean.totalPage;

}

//获取聊天记录
function FgetMsgData(pageNum, pageSize, toUser,type,groupId) {
    //表示该对象信息已读，移除Map
    if(type==1){
        NotRead(1,myname,toUser);
        haveRead(1,myname,toUser);
    }else{
        NotRead(2,myname,groupId);
        haveRead(2,myname,groupId);
    }
    //清空模板
    var DivArray = document.getElementById('ourMessageDiv').getElementsByTagName('div')
    var DivLen = DivArray.length;
    for (var i = 0; i < DivLen; i++) {
        DivArray[0].remove();
    }

    var jsonObj = new Object();
    jsonObj.currentPage = pageNum;
    jsonObj.row = pageSize;
    jsonObj.type = type;
    if (type == 1) {
        //私聊
        jsonObj.toUser = toUser;
    } else if (type == 2) {
        //群聊
        jsonObj.groupId = groupId;
    }
    var json = JSON.stringify(jsonObj);

    POST("/Msg/privateMsg",json,"getMsgData");
}

function getMsgData(data) {
    var pb = data.data;
    var msgList = pb.list;
    for(var i=0;i<msgList.length;i++) {
        var Msg = msgList[i];
        var div = document.createElement("div");
        var ourMessage = document.getElementById("ourMessageDiv");
        ourMessage.append(div);
        var span = document.createElement('span');
        div.append(span);
        var p = document.createElement('p');
        div.append(p);
        p.innerText = Msg.msg;
        if (Msg.fromUser == myname) {
            //是本人发的
            div.setAttribute("class", "me");
            span.setAttribute("class", "meName");
            span.innerText = Msg.fromUser;
        } else {
            //不是本人发的
            div.setAttribute("class", "other");
            span.setAttribute("class", "itsName");
            span.innerText = Msg.fromUser;
        }
    }
}

function addFriend(th) {
    var friendname = th.title;
    //执行添加好友的操作
    var inputMessage = myname+"请求添加您为好友";
    var socketMsg = {};
    socketMsg.user_GroupID="";
    socketMsg.msg = inputMessage;
    socketMsg.toUser = friendname;
    socketMsg.fromUser = myname;
    socketMsg.msgType = 3;
    var methodName ="addFriend";
    var Msg = {methodName:methodName,socketMsg:socketMsg}
    ws.send(JSON.stringify(Msg));
}

//添加群操作，等等再写
function addGroup(th) {

}

function private(th) {
    document.getElementById("connectNameDiv").innerText = th.value;
    document.getElementById("connectNameDiv").setAttribute("value",th.value);
    document.getElementById("Type").value=1;
    //点击聊天设置右端页面并获取消息记录并赋值让websokcet识别的东西
    FgetMsgData(1,5,th.value,1,null);
    if(document.getElementById("mesID").className=="selected"){
        FnewMsg();
    }
}

function public(th) {
    var group_Id = parseInt(th.value);
    document.getElementById("connectNameDiv").innerText = th.title;
    document.getElementById("connectNameDiv").setAttribute("value",group_Id);
    document.getElementById("Type").value=2;
    //点击聊天设置右端页面并获取消息记录并赋值让websokcet识别的东西
    FgetMsgData(1,5,null,2,group_Id);
}

//查询所有最近联系人和未接受消息的个数
function FnewMsg() {
    document.getElementById('mesID').setAttribute("class","selected");
    document.getElementById('groupsID').classList.remove('selected');
    document.getElementById('connectsID').classList.remove('selected');
    //动态创建最近联系人列表,先清空再创建
    var LiArray = document.getElementById('messageListUl').getElementsByTagName('li');
    var LiLen = LiArray.length;
    for (var i = 0; i < LiLen; i++) {
        LiArray[0].remove();
    }
    //因为要实时更新，所以用websocket
    newMsg();

}

function newMsg() {
    var socketMsg = {};
    socketMsg.fromUser = myname;
    var Msg = {methodName:"recentContacts",socketMsg:socketMsg}
    ws.send(JSON.stringify(Msg));
}

function recentContacts(WsInfo) {
    //拿到了好友类集合
    var myFriendList = WsInfo.result;
    if(myFriendList.length>0) {
        for (var i = 0; i < myFriendList.length; i++) {
            //展示好友信息
            var myFriend = myFriendList[i];
            var li = document.createElement("li");
            li.setAttribute("class", "Contact Template");
            var messageList = document.getElementById("messageListUl");
            messageList.append(li);
                //还需要显示昵称,改了之后会有问题
            if(myFriend.type==1){
                //私聊
                if(myFriend.user_nickname!=null){
                    var show = myFriend.toUser + "(" + myFriend.user_nickname + ")";
                }else{
                    var show = myFriend.toUser;
                }

            }else{
                //群聊
                if(myFriend.user_nickname!=null){
                    var show =  myFriend.user_nickname+ "(" + myFriend.fromUser+ ")";
                }else{
                    var show = myFriend.fromUser;
                }
                NotRead(2,myname,myFriend.toUser);
            }

        li.innerHTML = show + "<span >"+myFriend.mid+"</span> <button onclick='private(this)' value=" + (myFriend.toUser) + ">聊天</button>"

        }

    }

}

function FupdateNickname(th) {
    var word =  window.prompt("请输入要修改的昵称");
    var jsonObj=new Object();
    jsonObj.friendName=th.value;
    jsonObj.nickname=word;
    var json= JSON.stringify(jsonObj);
    POST("/friend/addNickname",json,"updateNickname");
}

function updateNickname(data) {
    FgetMyFriends();
}

//读取消息，移除Map，
function haveRead(type,key1,key2) {
    //key1是接受者 key2是发送者
    var socketMsg = {};
    if(type==1){
        //读取私聊信息
        socketMsg.user_GroupID=-1;
        socketMsg.toUser = myname;
        socketMsg.fromUser = key2;
    }else if(type==2){
        socketMsg.toUser = myname;
        socketMsg.user_GroupID=key2;
    }
    var Msg = {methodName:"removeMap",socketMsg:socketMsg}
    ws.send(JSON.stringify(Msg));
}
