//发送异步请求获取验证码
function getCheckCode() {
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xhttp.open("GET", "/user/checkMailCode", true);
//3.发送请求
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var data = JSON.parse(xhttp.responseText);
            console.log(data);
            if (data.flag) {
                alert(data.errorMsg);
            } else {
                //修改失败
                alert(data.errorMsg);
            }
        }
    }
}

    var reg_password=/^[^\s]{6,20}$/
    var newPassword;
    var newPassword1;
    //校验密码
    function checkPassword(){
        //1.获取密码
        newPassword= document.getElementById("newPassword").value;
        var flag=reg_password.test(newPassword);
        if(flag){
            //密码格式正确
            document.getElementById("newPassword").style.border="3px solid green";
        }else{
            //密码格式错误
            document.getElementById("newPassword").style.border="3px solid red";
        }
        return flag;
    }

    function checkPassword1(){
        //1.获取密码
        newPassword1= document.getElementById("newPassword1").value;
        var flag=reg_password.test(newPassword1);
        if(flag){
            //密码格式正确
            document.getElementById("newPassword1").style.border="3px solid green";
        }else{
            //密码格式错误
            document.getElementById("newPassword1").style.border="3px solid red";
        }
        return flag;
    }

    //校验密码
    function checkPassword1(){
        //1.获取密码
        newPassword1= document.getElementById("newPassword1").value;
        var flag1=reg_password.test(newPassword1);
        if(flag1){
            //密码格式正确
            document.getElementById("newPassword1").style.border="3px solid green";
        }else{
            //密码格式错误
            document.getElementById("newPassword1").style.border="3px solid red";
        }

        return flag1;
    }

    //提交时校验
    function fun() {
        if (checkPassword() && checkPassword1()) {
            //校验通过，发送请求
            var jsonObj = new Object();
            jsonObj.newPassword = newPassword;
            jsonObj.newPassword1 = newPassword1;
            jsonObj.prePassword = document.getElementById("prePassword").value;
            jsonObj.checkcode= document.getElementById("checkcode").value;
            var data;
            //将js对象转化成Json对象
            var json = JSON.stringify(jsonObj);
            console.log(json);
            var xhttp;
            if (window.XMLHttpRequest) {
                xhttp = new XMLHttpRequest();
            } else {
                // code for IE6, IE5
                xhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }

            xhttp.open("POST", "/user/updatePassword", true);
//3.发送请求
            xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xhttp.send(json);
            xhttp.onreadystatechange = function () {
                if (xhttp.readyState == 4 && xhttp.status == 200) {
                    data = JSON.parse(xhttp.responseText);
                    console.log(data);
                    if (data.flag) {
                        //修改成功，跳转登录页面
                        alert("修改成功，请按确定重新登录");

                    } else {
                        //修改失败
                        if(data.errorMsg==null){
                            alert("请输入验证码");
                        }else{
                            alert(data.errorMsg);
                        }

                    }
                }
            }
        }
    }

    document.getElementById("newPassword1").onblur=checkPassword1;
    document.getElementById("newPassword").onblur=checkPassword;

var check = document.getElementById('check');
check.addEventListener('click',function(){
    this.disabled = 'disabled';
    var count =30;
    //获取验证码
    getCheckCode();
    var timer = setInterval(function(){
        check.value = '再次点击获取验证码(' + count +')';
        check.style.backgroundColor = '#38e';
        check.style.color = '#fff';
        count--;
        if(count<1)
        {
            check.style.backgroundColor = "#e1e2e3";
            check.style.color = '#black';
            check.value = '点击获取验证码';
            check.disabled="";
            clearInterval(timer);
        }
    },1000);
},false)
