<%-- 
    Document   : ImageViewJSP
    Created on : Jul 1, 2020, 10:58:14 PM
    Author     : jiang
--%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>JServlet ImageView</title>
<!--        <link rel="stylesheet" href="style/ImageView.css">-->
        <style>
            *{margin: 0;padding: 0;}
            #images{
                position: relative;border: 1px solid gray;
                width: 10000px;
                margin: 20px auto;
                height: 320px;
                overflow: hidden;
            }
            #images ul{
                position: absolute;
                left: 0;
            }
            #images ul li{
                padding: 5px;
                list-style: none;
                width: 360px;
                height: 300px;
                float: left;
            }
            #images ul li img{
                width: 100%;
                height: 100%;
            }
        </style>
    </head>
    <body>
        <script type="text/javascript">
            window.onload = function () {
                var oDiv = document.getElementById('images');
                var oUl = oDiv.getElementsByTagName('ul')[0];
                var aLi = oUl.getElementsByTagName('li');
                var iSpeed = 1;
                var timer = null;
                oUl.innerHTML += oUl.innerHTML + oUl.innerHTML;
                oUl.style.width = aLi[0].offsetWidth * aLi.length + 'px';
                function Slider() {
                    if (oUl.offsetLeft < -oUl.offsetWidth / 2) {
                        oUl.style.left = 0;
                    } else if (oUl.offsetLeft > 0) {
                        oUl.style.left = -oUl.offsetWidth / 2 + 'px';
                    }
                    oUl.style.left = oUl.offsetLeft - iSpeed + 'px';
                }
                timer = setInterval(Slider, 30);
            };
        </script>

        <%
            String basePath = request.getScheme()
                    + "://"
                    + request.getServerName()
                    + ":"
                    + request.getServerPort()
                    + request.getContextPath()
                    + "/";

            pageContext.setAttribute("basePath", basePath);
        %>
    <base href = "<%=basePath%>">

    <section class="jumbotron text-center">
        <div class="container">
            <div class="greeting">
                <h1>Image Gallery</h1>
                <p class="lead text-muted">Please feel free to filter the photos as needed. Have a good day!.</p>
            </div>

            <div class="boardForm">
                <form action="ImageView" method="post">
                    <c:forEach items="${requestScope.boards}" var="board">
                        <c:if test="${requestScope.action == 'get'}">
                            <input type="checkbox" name="checkbox" value="${board.id}" checked="true">
                            <label for="checkbox"> ${board.id} : ${board.name}&nbsp&nbsp&nbsp&nbsp&nbsp</label>
                        </c:if>

                        <c:if test="${requestScope.action != 'get'}">
                            <input type="checkbox" name="checkbox" value="${board.id}" 
                                   <c:forEach items="${requestScope.selectedBoards}" var="selectedBoard">
                                       <c:if test="${selectedBoard == board.id}">
                                           checked="true"
                                       </c:if>
                                   </c:forEach>
                                   >
                            <label for="checkbox"> ${board.id} : ${board.name}&nbsp&nbsp&nbsp&nbsp&nbsp</label>
                        </c:if>


                    </c:forEach>
                    <input type="submit" value="confirm">

                </form>
            </div>

            <div id="images">
                <ul>
                    <c:forEach items="${requestScope.entityFileNames}" var="fileName">
                        <li><img class="imageThumb" src="Image/${fileName}" ></li>
                        </c:forEach>
                </ul>
            </div>
            </body>
            </html>
