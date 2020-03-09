function fun() {
        var jsonObj=new Object();
        jsonObj.username=document.getElementById("username").value;
        jsonObj.password=document.getElementById("password").value;
        jsonObj.checkcode=document.getElementById("checkcode").value;
        //将js对象转化成Json对象

        var json= JSON.stringify(jsonObj);
        var data;
        //1.发送数据到服务器
        //校验通过，发送ajax请求，提交数据
        //1.创建对象
        var xhttp;
        if (window.XMLHttpRequest) {
            xhttp = new XMLHttpRequest();
        } else {
            // code for IE6, IE5
            xhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        //2.建立连接
        /*
        *参数：
        *  1.请求方式：GET、POST
        *      get方式，请求参数在URL后边拼接，send方法为空参
        *      post方式，请求参数在send方法中定义
        *  2.请求的url路径
        *  3.同步或异步请求
        * */
        xhttp.open("POST", "/user/login?t="+new Date(), true);
        //添加HTTP头部
        xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        //3.发送请求
        xhttp.send(json);

        xhttp.onreadystatechange = function() {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                data = JSON.parse(xhttp.responseText);
                console.log(data);
                if(data.flag==false){
                    document.getElementById("Error_Msg").innerHTML = data.errorMsg;
                }else{
                    location.href="MyChatInterface.html";
                }
            }
        }

}


function refreshCode() {
            //切换验证码
        //1.获取验证码的图片对象
        var img_check = document.getElementById("img_check");
        //2.设置其src属性，加时间戳
        img_check.src="/user/checkCode?time="+new Date().getTime();
    }


