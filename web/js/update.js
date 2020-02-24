window.onload=function (){

    //回显数据
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.open("GET", "/user/getMyself", true);
    //3.发送请求
    xhttp.send();
    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4 && xhttp.status == 200) {

             var data= JSON.parse(xhttp.responseText);
             var user=data.data;

            document.getElementById("username").value=user.username;
            document.getElementById("name").value=user.name;
           document.getElementById("birthday").value=user.birthday;
            document.getElementById("telephone").value=user.telephone;
            document.getElementById("email").value=user.email;
            if(user.gender=="男"){
                document.getElementById("man").setAttribute("checked","checked");
            }else{

                document.getElementById("woman").setAttribute("checked","checked");
            }

            document.getElementById("name").onblur=checkName;
            document.getElementById("telephone").onblur=checkTel;
            document.getElementById("birthday").onblur=checkBir;
        }


    }

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

//校验生日
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

//修改数据
var jsonObj=new Object();
function fun() {
    if( checkName()&&checkTel()&&checkBir()){
        console.log("正在调用此方法");
        jsonObj.name=document.getElementById("name").value;
        jsonObj.username=document.getElementById("username").value;
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

        var xhttp;
        if (window.XMLHttpRequest) {
            xhttp = new XMLHttpRequest();
        } else {
            // code for IE6, IE5
            xhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }


        xhttp.open("POST", "/user/updateMessage", true);
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
                    alert(data);
                }
            }
        }
    }
}
