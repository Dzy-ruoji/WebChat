
window.onload=function () {
    //定义pageBean变量
    var pageBean;
    //第一次默认第一页，展示5行
    getData(1, 5);

    //获取数据(根据传递过来的页码，来获取该页数据，并展示在页面上，并生成对应的分页条)
    function getData(pageNum, pageSize) {
        var jsonObj=new Object();
        jsonObj.currentPage=pageNum;
        jsonObj.row=pageSize;
        var json= JSON.stringify(jsonObj);


        var data;
        //干掉div(dataTable)下除第一个以外的所有ul
        var UiArray = document.getElementsByTagName("ul");
        var UiLen = document.getElementsByTagName("ul").length;
        for (var i = 0; i < UiLen; i++) {
            if (i != 0) {

                UiArray[1].remove();
            }
        }

        //清空分页条里的页码
        var AArrary = document.getElementsByTagName("a");
        var ALen = document.getElementsByTagName("a").length;
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

        xhttp.open("POST", "/user/getUserList", true);
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

                    //------------------>展示数据
                    //获取该页对应数据
                    var userList = pageBean.list;
                    //userList -->  [{},{},{}]
                    var index = 0;
                    var LiLen = document.getElementsByTagName("li").length;
                    for (var i = 0; i < userList.length; i++) {
                        //当前要展示的用户信息
                        var user = userList[i];
                        var ul = document.createElement("ul");
                        ul.setAttribute("class", "clearfix");
                        var dataTable = document.getElementById("dataTable");
                        dataTable.append(ul);
                        var li = [];
                        for (var j = 0; j < LiLen; j++) {
                            li[j] = document.createElement("li");
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
                            li[j++].innerHTML = "" + user.email;
                            li[j++].innerHTML = "操作";
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
                          getData(pageBean.currentPage - 1, 5);
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
                               getData(this.innerText, 5);
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
                           getData(pageBean.currentPage + 1, 5);
                            }
                        }
                        pageBox.append(nextPage);

                        document.getElementById("totalCount").innerText = pageBean.totalCount;
                        document.getElementById("totalPage").innerText =pageBean.totalPage;
                }

            }



        }

    }
}