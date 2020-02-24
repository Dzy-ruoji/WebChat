
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
        document.getElementById("username").innerText=user.username;
        document.getElementById("name").innerText=user.name;
        document.getElementById("birthday").innerText=user.birthday;
        document.getElementById("telephone").innerText=user.telephone;
        document.getElementById("email").innerText=user.email;
        document.getElementById("gender").innerText=user.gender;

        document.getElementById("img").src="../Image/"+user.src;


    }

}

function upload() {
    var jsonObj=new Object();
    jsonObj.src="获取绝对路径";
    var json= JSON.stringify(jsonObj);

    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.open("POST", "/user/uploadImg", true);
//3.发送请求
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send(json);
    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4 && xhttp.status == 200) {

            var data= JSON.parse(xhttp.responseText);

            var Image_src=data.data;

           //上传的时候有延时，不能立马显示出上传成功的头像

            document.getElementById("img").src="../Image/"+Image_src;
        }

    }

}