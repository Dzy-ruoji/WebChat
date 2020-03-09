window.setInterval("showOnline();",1000);
window.setInterval("showContext();",1000);
window.onload=function () {
    showOnline();




}

//获取在线用户列表
function showOnline() {
    //发送异步请求，获取在线人员列表

    //将js对象转化成Json对象
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET", "/user/updatePassword"+new Date().getTime(), true);
//3.发送请求
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            data = JSON.parse(xhttp.responseText);
            console.log(data);
        }
    }
}

//获取消息
function showContext() {
    //发送异步请求，获取在线人员列表

    //将js对象转化成Json对象
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET", "/user/updatePassword"+new Date().getTime(), true);
//3.发送请求
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            data = JSON.parse(xhttp.responseText);
            console.log(data);
        }
    }
}

//判断用户有没有被踢
function check() {
    //发送异步请求，获取在线人员列表

    //将js对象转化成Json对象
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET", "/user/check"+new Date().getTime(), true);
//3.发送请求
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            data = JSON.parse(xhttp.responseText);
            console.log(data);
        }
    }
}