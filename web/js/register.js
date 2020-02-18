/*
    表单校验(正则表达式)
        1.用户名，长度8到20位
        2.密码：单词字符，长度8到20位
        3.邮箱：邮件格式
        4.姓名： 非空
        5.手机号：手机号格式
        6.出生日期： 非空
        7.验证码：
        表单提交时，调用所有校验方法
        某一组件失去焦点时，调用对应的校验方法
*/
//校验用户名,
function checkUsername() {
    //1.获取用户名
    var username=document.getElementById("username").value;
    var flag
    if(username!=''){
        //用户名合法
        document.getElementById("username").style.border="3px solid green";
        flag=true;
    }else{
        //用户名非法
        document.getElementById("username").style.border="3px solid red";
        flag=false;
    }

    return flag;
}

//校验密码
function checkPassword(){
    //1.获取密码
    var password=document.getElementById("password").value;
    //2.定义正则
    var reg_password=/^[^\s]{6,20}$/
    //3.判断，给出提示信息
    var flag=reg_password.test(password);
    if(flag){
        //密码格式正确
        document.getElementById("password").style.border="3px solid green";
    }else{
        //密码格式错误
        document.getElementById("password").style.border="3px solid red";
    }
    return flag;
}

//校验邮箱
function checkEmail(){
    //1.绑定邮箱
    var email=document.getElementById("email").value;
    //2.定义正则
    var reg_email=/^\w+@\w+\.\w+$/;
    //3.判断
    var flag=reg_email.test(email);
    if(flag){
        document.getElementById("email").style.border="3px solid green";
    }else {
        document.getElementById("email").style.border="3px solid red";
    }
    return flag;
}

//校验姓名
function checkName(){
    //1.绑定姓名
    var name=document.getElementById("name").value;
    //2.判断非空
    var flag
    if(name==''){
        document.getElementById("name").style.border="3px solid red";
        flag=false;
    }else{
        document.getElementById("name").style.border="3px solid green";
        flag=true;
    }
    return flag;
}

//校验手机号
function checkTel(){
    //1.绑定手机号
    var telephone=document.getElementById("telephone").value;
    //2.定义正则
    var reg_tel=/^\d{11}$/;
    //3.判断
    var flag=reg_tel.test(telephone);
    if(flag){
        document.getElementById("telephone").style.border="3px solid green";
    }else {
        document.getElementById("telephone").style.border="3px solid red";
    }
    return flag;
}

//校验手机号
function checkBir(){
    //1.绑定生日
    var birthday=document.getElementById("birthday").value;
    //2.判断生日
    var flag;
    if(birthday==""){
        document.getElementById("birthday").style.border="3px solid red";
           flag=false;
    }else {
        document.getElementById("birthday").style.border="3px solid green";
        flag=true;
    }
    return flag;
}



function fun() {
   if( checkUsername()&&checkPassword()&&checkEmail()&&checkName()&&checkTel()&&checkBir()){
        console.log("正在调用此方法");
        var jsonObj=new Object();
        jsonObj.name=document.getElementById("name").value;
        jsonObj.username=document.getElementById("username").value;
        jsonObj.password=document.getElementById("password").value;
        jsonObj.email=document.getElementById("email").value;
        jsonObj.telephone=document.getElementById("telephone").value;
        jsonObj.birthday=document.getElementById("birthday").value;
        var obj = document.getElementsByName("gender");
        var data;
        for (var i = 0; i < obj.length; i++) { //遍历Radio
            if (obj[i].checked) {
                jsonObj.gender= obj[i].value;
            }
        }
        //将js对象转化成Json对象
        var json= JSON.stringify(jsonObj);

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
        xhttp.open("POST", "/user/register?t="+new Date(), true);
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
                    location.href="login.html";
                }
            }
        }
    }
}

document.getElementById("username").onblur=checkUsername;
document.getElementById("password").onblur=checkPassword;
document.getElementById("email").onblur=checkEmail;
document.getElementById("name").onblur=checkName;
document.getElementById("telephone").onblur=checkTel;
document.getElementById("birthday").onblur=checkBir;


function refreshCode() {
    //切换验证码
    //1.获取验证码的图片对象
    var img_check = document.getElementById("img_check")
    //2.设置其src属性，加时间戳
    img_check.src="/checkCodeServlet?time="+new Date().getTime();
}
