window.onload=function () {
    show();
}
function show() {
    var xhttp;
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xhttp.open("GET", "/user/getMyself?"+new Date().getTime(), true);
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
            document.getElementById("gender").value=user.gender;
            document.getElementById("img").src="../Image/"+user.src;
            console.log("这是一开始的头像"+user.src);
        }
    }
}

function uploadFile (files, process){
        var file = files[0];
        var node = document.querySelector('#' + process);
        var name = file.name;
        var formatName=name.substring(name.length-3);
        console.log(formatName);

        if(formatName!="jpg"&&formatName!="bmp"&&formatName!="png"){
            alert("请选择正确格式的图片");
            return;
        }

        var formData = new FormData();
        formData.set('files', file);
        var request = new XMLHttpRequest();
        request.open("POST", "/user/uploadImg", true);
        if (request.upload){
            request.upload.addEventListener('progress', function(event){
                var percent  = ((event.loaded / event.total) * 100).toFixed(2);
                node.setAttribute('value', percent);
            }, false);
        }
        request.send(formData);
        request.onreadystatechange = function()	{
            if(request.readyState == 4 && request.status == 200){
                var message = JSON.parse(request.responseText);
                alert("修改成功，请稍后刷新更新头像");
            }
        }
    }

//校验文件
function checkFile(path){
    var ss = path.substr(path.lastIndexOf(".")).toUpperCase();//得到的是后缀名,且转换为大写
    if(ss == ".JPG" || ss ==".PNG"||ss==".bmp"){
        return true;
    }else{
        alert("上传文件格式错误");
        return false;
    }
}


