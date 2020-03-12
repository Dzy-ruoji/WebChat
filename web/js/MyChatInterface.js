//全局变量
//myname;我的名字
//friend;查询的字段名

//发送信息的时候只能读取到当前显示的值，要改
window.onload=function () {
    getMyself();

}

function chat() {
    if ('WebSocket' in window) {
        var wsUrl="ws://localhost:8080/chat/chatRoomSerController/"+myname;
        //客户端与服务端建立连接，建立连接后，会发生一个ws.open事件
        ws = new WebSocket(wsUrl);
        //连接成功后，啥都不干
        ws.onopen=function () {

        }

        //客户端收到服务器发送的消息,在线时的即时信息，不在线时的多条离线信息,难点
        ws.onmessage = function (message) {
            //先判断当前跟谁在聊天
            var connectNow = document.getElementById("connectNameDiv").getAttribute("value");
            var arr = message.data.split("&");
            if(arr[0] == 'notReadTotal'){
                //如果当前有在聊天则减去当前的已读记录
                var notReadTotal = arr[1];
                var sender;
                if(arr[2]=='user_GroupID'){
                     sender = arr[3];
                }else if(arr[2]=='fromUser'){
                     sender = arr[3];
                }
                if(connectNow==sender){

                    notReadTotal = notReadTotal-1;
                }
                document.getElementById("manySpan").innerText=notReadTotal;
                return;
            }

            var msgList=JSON.parse(message.data)
               //如果消息的发送者刚好是现在这个人,就显示到聊天页面
            var flag = false;
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
                if(flag){
                    var Msg = msgList;
                    var div = document.createElement("div");
                    var ourMessage = document.getElementById("ourMessageDiv");
                    ourMessage.append(div);
                    var span = document.createElement('span');
                    div.append(span);
                    var p = document.createElement('p');
                    div.append(p);
                    p.innerText = Msg.msg;
                    if(Msg.fromUser == myname){
                        //是本人发的
                        div.setAttribute("class", "me");
                        span.setAttribute("class","meName");
                        span.innerText = Msg.fromUser;
                    }else{
                        //不是本人发的
                        div.setAttribute("class", "other");
                        span.setAttribute("class","itsName");
                        span.innerText = Msg.fromUser;
                }
                        haveRead(msgList.type,connectNow);
                    //显示未读消息数
                    //滚动条
                    var msg = document.getElementById("ourMessageDiv");
                    //差值 溢出的高度减掉可视高度
                    var distance = msg.scrollHeight-msg.offsetHeight;
                    //将差值赋给滚动条的高度
                    msg.scrollTop = distance;
            }

        }
        ws.onerror = function () {
            alert("WebSocket连接发生未知错误");
        }
    }else {
        alert('当前浏览器不支持WebSocket');
    }
}

//获取某个用户输入的聊天内容，并发送给服务器，让服务端广播给所有人（群聊）
function getMessage() {
    var toUser=null;
    var user_GroupID=null;
    var type = document.getElementById("Type").value;
    var inputMessage = document.getElementById("inputMessage").value;
    var fromUser = myname;
    //暂且用户只能发送纯文本，以后再改
    var msgType = 1;
    if(type==1){
        //私聊
         toUser = document.getElementById("connectNameDiv").innerText;
    }else{
        user_GroupID = +document.getElementById("connectNameDiv").getAttribute("value");

    }
    //根据id获取要发送的对象
   // var Msg = {msg:inputMessage,toUser:toUser,fromUser:fromUser,type:type,msgType:msgType,user_GroupID:user_GroupID};
    var socketMsg = {};
    socketMsg.user_GroupID=1;
    //测试
    var Msg = {methodName:"privateChat",socketMsg:socketMsg}


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


//获取我自身信息
function getMyself() {
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET", "/user/getMyself?t="+new Date(), true);
    //添加HTTP头部
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    //3.发送请求
    xhttp.send();

    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var data = JSON.parse(xhttp.responseText);
            var user = data.data;
            if(user!=null){
                myname=user.username;

                chat();
            }else{
                alert("您尚未登录");
            }
        }
    }
}

//获取好友列表,动态创建好友列表
function getMyFriends() {
    document.getElementById('connectsID').setAttribute("class","selected");
    document.getElementById('mesID').classList.remove('selected');
    document.getElementById('groupsID').classList.remove('selected');
    //动态创建好友列表,先清空再创建
    var LiArray = document.getElementById('messageListUl').getElementsByTagName('li');
    var LiLen = LiArray.length;

    for (var i = 0; i < LiLen; i++) {
        LiArray[0].remove();
    }

    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET", "/friend/getMyFriend?t="+new Date().getTime(), true);
    //添加HTTP头部
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    //3.发送请求
    xhttp.send();
    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var data = JSON.parse(xhttp.responseText);
            //拿到了好友类集合
            var myFriendList = data.data;
            if(myFriendList.length>0){
                for(var i=0;i<myFriendList.length;i++){
                    //展示好友信息
                    var myFriend = myFriendList[i];
                    var li = document.createElement("li");
                    li.setAttribute("class", "Contact Template");
                    var messageList = document.getElementById("messageListUl");
                    messageList.append(li);
                    if(myFriend.friend_1==myname){
                        //说明myFriend.friend_2是好友姓名，myFriend.friendNickname_2是我给好友取得昵称
                        //还需要显示昵称,改了之后会有问题
                        if(myFriend.friendNickname_2!=null){
                            var show = myFriend.friend_2+"("+myFriend.friendNickname_2+")";
                            li.innerHTML = show +" <button>删除</button> <button onclick='updateNickname(this)' value="+(myFriend.friend_2)+">修改</button> <button onclick='private(this)' value="+(myFriend.friend_2)+">聊天</button>"
                          }else{
                                li.innerHTML = myFriend.friend_2+"<button>删除</button> <button onclick='updateNickname(this)' value="+(myFriend.friend_2)+">修改</button> <button onclick='private(this)'  value="+(myFriend.friend_2)+">聊天</button>"
                          }
                        }else {
                            if(myFriend.friendNickname_1!=null){
                                var show = myFriend.friend_1+"("+myFriend.friendNickname_1+")";
                             li.innerHTML = show + "<button>删除</button> <button onclick='updateNickname(this)'  value="+(myFriend.friend_1)+">修改</button> <button onclick='private(this)' value="+(myFriend.friend_1)+">聊天</button>"
                          }else{
                                li.innerHTML = myFriend.friend_1+"<button>删除</button> <button onclick='updateNickname(this)'  value="+(myFriend.friend_1)+">修改</button> <button onclick='private(this)' value="+(myFriend.friend_1)+">聊天</button>"
                        }

                    }

                   }
            }

            }
        }
}

//获取群列表，动态创建群组列表
function getMyGroups() {
    document.getElementById('groupsID').setAttribute("class","selected");
    document.getElementById('mesID').classList.remove('selected');
    document.getElementById('connectsID').classList.remove('selected');
    //动态创建好友列表,先清空再创建
    var LiArray = document.getElementById('messageListUl').getElementsByTagName('li');
    var LiLen = LiArray.length;
    for (var i = 0; i < LiLen; i++) {
        LiArray[0].remove();
    }

    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET", "/groupChat/getMyGroups?t="+new Date().getTime(), true);
    //添加HTTP头部
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    //3.发送请求
    xhttp.send();
    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var data = JSON.parse(xhttp.responseText);
            var myGroupsList = data.data.user_groupsList;
            if(myGroupsList.length>0){
                for(var i=0;i<myGroupsList.length;i++){
                    //展示群组信息
                    var myGroups = myGroupsList[i];
                    var li = document.createElement("li");
                    li.setAttribute("class", "Contact Template");
                    var messageList = document.getElementById("messageListUl");
                    messageList.append(li);
                    li.innerHTML = myGroups.user_Groups.ug_Name+"<button>退出</button><button>修改</button> <button  onclick='public(this)' title="+myGroups.user_Groups.ug_Name+" ''  value="+myGroups.user_Groups.ug_ID+">聊天</button>"
                }

            }

        }
    }
}

//friend为全局变量
function searchMyFriend() {
    var pageBean;
     friendName = document.getElementById("searchName").value;
    if(friendName!=""&&friendName.length>1){
        getData(1,5,friendName);
        document.getElementById("test").style.display = 'block';
    }else {
        getData(1, 5);
        document.getElementById("test").style.display = 'block';
        document.getElementById("search").onclick=function () {
            var name = document.getElementById("username").value;
            if(name!=''){
                friendname = name;
                getData(1, 5,friendname);
            }
            return false;
        };

    }

}

//获取数据(根据传递过来的页码，来获取该页数据，并展示在页面上，并生成对应的分页条)
function getData(pageNum, pageSize,friendname) {
    var jsonObj=new Object();
    jsonObj.currentPage=pageNum;
    jsonObj.row=pageSize;
    if(username!="undefined"){
        jsonObj.username=friendname;
    }

    var json= JSON.stringify(jsonObj);
    var data;
    //干掉div(dataTable)下除第一个以外的所有ul
    var UiArray = document.getElementById("dataTable").getElementsByTagName('ul');
    var UiLen = UiArray.length;
    for (var i = 0; i < UiLen; i++) {
        if (i != 0) {
            UiArray[1].remove();
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

    //发送ajax请求获取数据
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("POST","/user/findUserBySearchName", true);
//添加HTTP头部
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//3.发送请求
    xhttp.send(json);

    xhttp.onreadystatechange = function () {

        if (xhttp.readyState == 4 && xhttp.status == 200) {
            data = JSON.parse(xhttp.responseText);

            if (!data.flag) {
                alert("请求迷路了......");
            } else {

                //表示分页对象
                pageBean = data.data;
                console.log(pageBean);
                //------------------>展示数据
                //获取该页对应数据
                var userList = pageBean.list;
                //userList -->  [{},{},{}]
                var index = 0;
                var LiLen = document.getElementById("lif").getElementsByTagName("li").length;
                for (var i = 0; i < userList.length; i++) {
                    //当前要展示的用户信息
                    user = userList[i];
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
                        li[j++].innerHTML = "<label><input value=\"2\" class=\"a-checkbox\" type=\"checkbox\"><div class=\"b-checkbox\"></div></label>"
                        li[j++].innerHTML = "" + ((++index)+(pageNum-1)*pageSize);
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
                        getData(pageBean.currentPage - 1, 5,friendname);
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
                            getData(this.innerText, 5,friendname);
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
                        getData(pageBean.currentPage + 1, 5,username);
                    }
                }
                pageBox.append(nextPage);

                document.getElementById("totalCount").innerText = pageBean.totalCount;
                document.getElementById("totalPage").innerText =pageBean.totalPage;
            }

        }

    }

}

//获取聊天记录
function getMsgData(pageNum, pageSize, toUser,type,groupId) {
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

    //发送ajax请求获取数据
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("POST", "/Msg/privateMsg", true);
//添加HTTP头部
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//3.发送请求
    xhttp.send(json);

    xhttp.onreadystatechange = function () {

        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var data = JSON.parse(xhttp.responseText);
            var pb = data.data;
            var msgList = pb.list;
            for(var i=0;i<msgList.length;i++){
                var Msg = msgList[i];
                var div = document.createElement("div");
                var ourMessage = document.getElementById("ourMessageDiv");
                ourMessage.append(div);
                var span = document.createElement('span');
                div.append(span);
                var p = document.createElement('p');
                div.append(p);
                p.innerText = Msg.msg;


                if(Msg.fromUser == myname){
                    //是本人发的
                    div.setAttribute("class", "me");
                    span.setAttribute("class","meName");
                    span.innerText = Msg.fromUser;
                }else{
                    //不是本人发的
                    div.setAttribute("class", "other");
                    span.setAttribute("class","itsName");
                    span.innerText = Msg.fromUser;
                }
            }

        }
    }
}

function addFriend(th) {
    var friendname = th.title;
    //执行添加好友的操作
    //var inputMessage = document.getElementById("文本框的id").value;
    // var toUser = document.getElementById('toUser').value;
    var inputMessage = myname+"请求添加您为好友";
    //根据id获取要发送的对象
    var toUser = friendname;
    var User_GroupID = "";
    var fromUser =myname;
    var msgType = 3;//添加请求
    var socketMsg = {msg:inputMessage,toUser:toUser,fromUser:fromUser,user_GroupID:User_GroupID,msgType:msgType};
    ws.send(JSON.stringify(socketMsg));
}

function private(th) {
    document.getElementById("connectNameDiv").innerText = th.value;
    document.getElementById("connectNameDiv").setAttribute("value",th.value);
    document.getElementById("Type").value=1;
    //点击聊天设置右端页面并获取消息记录并赋值让websokcet识别的东西
    getMsgData(1,5,th.value,1,null);

}

function public(th) {
    var group_Id = parseInt(th.value);
    document.getElementById("connectNameDiv").innerText = th.title;
    document.getElementById("connectNameDiv").setAttribute("value",group_Id);
    document.getElementById("Type").value=2;
    //点击聊天设置右端页面并获取消息记录并赋值让websokcet识别的东西
    getMsgData(1,5,null,2,group_Id);
}
//修改好友昵称
function updateNickname(th) {
    var word =  window.prompt("请输入要修改的昵称");
        var xhttp;
        if (window.XMLHttpRequest) {
            xhttp = new XMLHttpRequest();
        } else {
            // code for IE6, IE5
            xhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        var jsonObj=new Object();
        jsonObj.friendName=th.value;
        jsonObj.nickname=word;
        var json= JSON.stringify(jsonObj);
        xhttp.open("POST", "/friend/addNickname?t="+new Date().getTime(), true);
        //添加HTTP头部
        xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        //3.发送请求
        xhttp.send(json);

        xhttp.onreadystatechange = function() {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
               getMyFriends();
            }
        }
}
//处理未读消息
function readNew(msgList) {
    alert("未读消息来了"+msgList);
    //先加点提示给消息通知列表一栏提示未读，累加法，从数据库里面查算了

    //用户点击消息通知时会显示未读的消息


}

//处理已读消息
function haveRead(type,connectNow) {


}